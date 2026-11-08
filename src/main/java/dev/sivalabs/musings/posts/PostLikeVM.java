package dev.sivalabs.musings.posts;

public record PostLikeVM(Long postId, long count, boolean liked) {}
