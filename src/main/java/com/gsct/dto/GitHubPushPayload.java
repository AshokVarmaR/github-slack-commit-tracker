package com.gsct.dto;

import java.util.List;

import lombok.Data;

@Data
public class GitHubPushPayload {
    private Pusher pusher;
    private List<GitHubCommit> commits;
}
