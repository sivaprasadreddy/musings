package dev.sivalabs.musings.posts;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreatePostRequest(
        @NotBlank(message = "Enter a title") @Size(max = 200, message = "Title must be at most 200 characters")
        String title,

        @NotBlank(message = "Enter your message")
        @Size(max = 10000, message = "Content must be at most 10,000 characters")
        String content) {}
