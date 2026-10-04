package dev.sivalabs.musings.posts;

import java.time.Instant;

public record PostVM(Long id, String title, String content, String authorName, Instant createdAt) {}
