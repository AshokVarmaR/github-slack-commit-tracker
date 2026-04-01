package com.gsct.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.gsct.model.Author;
import com.gsct.model.Commit;
import com.gsct.repository.AuthorRepository;
import com.gsct.repository.CommitRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommitService {

    private final AuthorRepository authorRepo;
    private final CommitRepository commitRepo;
    private final WebClient webClient;
    
    @Value("${slack.webhook.url}")
    private String webhookUrl;
    
    public void saveCommit(String authorName, List<String> commitMessages) {
        Author author = new Author();
        author.setName(authorName);

        List<Commit> commits = commitMessages.stream().map(msg -> {
            Commit commit = new Commit();
            commit.setMessage(msg);
            commit.setAuthor(author);
            return commit;
        }).toList();

        author.setCommits(commits);
        authorRepo.save(author);

        sendSlackNotification(authorName, commitMessages);
    }

    private void sendSlackNotification(String authorName, List<String> commitMessages) {
        String message = authorName + " pushed " + commitMessages.size() + " commits:\n";
        for (String msg : commitMessages) {
            message += "- " + msg + "\n";
        }

        webClient.post()
                .uri(webhookUrl)
                .bodyValue("{\"text\":\"" + message + "\"}")
                .retrieve()
                .bodyToMono(String.class)
                .subscribe();
    }
}