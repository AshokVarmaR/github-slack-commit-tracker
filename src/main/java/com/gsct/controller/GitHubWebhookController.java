package com.gsct.controller;

import com.gsct.service.CommitService;
import lombok.Data;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/github-webhook")
public class GitHubWebhookController {

    private final CommitService commitService;

    public GitHubWebhookController(CommitService commitService) {
        this.commitService = commitService;
    }

    @PostMapping
    public String handlePushEvent(@RequestBody GitHubPushPayload payload) {
        // Extract author
        String authorName = payload.getPusher().getName();

        // Extract commit messages
        List<String> commitMessages = payload.getCommits()
                .stream()
                .map(GitHubCommit::getMessage)
                .collect(Collectors.toList());

        // Save to DB and send Slack notification
        commitService.saveCommit(authorName, commitMessages);

        return "Webhook received successfully!";
    }

    // DTOs to map GitHub payload
    @Data
    public static class GitHubPushPayload {
        private Pusher pusher;
        private List<GitHubCommit> commits;
    }

    @Data
    public static class Pusher {
        private String name;
    }

    @Data
    public static class GitHubCommit {
        private String message;
    }
}