package dev.sivalabs.musings.posts;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
class PostLikeRepository {
    private final JdbcClient jdbc;

    PostLikeRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    Map<Long, PostLikeVM> findForPosts(List<Long> postIds, Long userId) {
        if (postIds.isEmpty()) {
            return Map.of();
        }
        return jdbc
                .sql("""
                        select p.id, count(l.user_id) as like_count,
                               coalesce(bool_or(l.user_id = :userId), false) as liked
                        from posts p left join post_likes l on l.post_id = p.id
                        where p.id in (:postIds)
                        group by p.id
                        """)
                .param("postIds", postIds)
                .param("userId", userId == null ? -1L : userId)
                .query((rs, row) -> new PostLikeVM(rs.getLong("id"), rs.getLong("like_count"), rs.getBoolean("liked")))
                .list()
                .stream()
                .collect(Collectors.toMap(PostLikeVM::postId, Function.identity()));
    }

    void like(Long postId, Long userId) {
        jdbc.sql("insert into post_likes(post_id, user_id) values (:postId, :userId) on conflict do nothing")
                .param("postId", postId)
                .param("userId", userId)
                .update();
    }

    void unlike(Long postId, Long userId) {
        jdbc.sql("delete from post_likes where post_id = :postId and user_id = :userId")
                .param("postId", postId)
                .param("userId", userId)
                .update();
    }
}
