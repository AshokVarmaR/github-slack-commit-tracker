package com.gsct.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gsct.dto.GitHubCommit;
import com.gsct.dto.GitHubPushPayload;
import com.gsct.service.CommitService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/github-webhook")
public class GitHubWebhookController {
	
	// Comit service methods

    private final CommitService commitService;

    @PostMapping
    public String handlePushEvent(@RequestBody GitHubPushPayload payload) {
        
        String authorName = payload.getPusher().getName();

        List<String> commitMessages = payload.getCommits()
                .stream()
                .map(GitHubCommit::getMessage)
                .collect(Collectors.toList());
       
        commitService.saveCommit(authorName, commitMessages);

        return "Webhook received successfully!";
    }
}