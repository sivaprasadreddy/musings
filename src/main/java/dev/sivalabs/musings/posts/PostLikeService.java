package dev.sivalabs.musings.posts;

import dev.sivalabs.musings.shared.ResourceNotFoundException;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class PostLikeService {
    private final PostRepository posts;
    private final PostLikeRepository likes;

    PostLikeService(PostRepository posts, PostLikeRepository likes) {
        this.posts = posts;
        this.likes = likes;
    }

    @Transactional(readOnly = true)
    public Map<Long, PostLikeVM> findForPosts(List<Long> postIds, Long userId) {
        return likes.findForPosts(postIds, userId);
    }

    @Transactional
    public PostLikeVM setLiked(Long postId, Long userId, boolean liked) {
        // Serialize changes to a post so the returned count includes this action.
        posts.findForUpdate(postId)
                .orElseThrow(() -> new ResourceNotFoundException("This post is no longer available"));
        if (liked) {
            likes.like(postId, userId);
        } else {
            likes.unlike(postId, userId);
        }
        return likes.findForPosts(List.of(postId), userId).get(postId);
    }
}
