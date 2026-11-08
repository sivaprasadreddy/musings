package dev.sivalabs.musings.posts;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import dev.sivalabs.musings.BaseIT;
import dev.sivalabs.musings.users.Role;
import dev.sivalabs.musings.users.SecurityUser;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

@Sql({"/test-data.sql", "/like-test-data.sql"})
class PostLikeTests extends BaseIT {
    private final PostLikeService likes;
    private final SecurityUser author =
            new SecurityUser(1L, "Siva", "sivaprasadreddy.k@gmail.com", "", Role.ROLE_ADMIN);
    private final SecurityUser reader = new SecurityUser(2L, "Reader", "reader@example.com", "", Role.ROLE_USER);

    @Autowired
    PostLikeTests(PostLikeService likes) {
        this.likes = likes;
    }

    @Test
    void shouldLikeOwnPostAndUnlikeIdempotently() throws UnsupportedEncodingException {
        for (int i = 0; i < 2; i++) {
            var result = mvc.post()
                    .uri("/posts/24/like")
                    .with(user(author))
                    .with(csrf())
                    .header("HX-Request", "true")
                    .param("userId", "2")
                    .exchange();
            assertThat(result).hasStatusOk().hasViewName("posts/likes :: likes");
            assertThat(result.getResponse().getContentAsString())
                    .contains(
                            "id=\"post-likes-24\"",
                            ">1</span>",
                            "aria-label=\"Unlike\"",
                            "♥",
                            "hx-post=\"/posts/24/unlike\"",
                            "hx-target=\"closest .post-likes\"",
                            "hx-swap=\"outerHTML\"",
                            "name=\"_csrf\"")
                    .doesNotContain("<html", "Post 24", "Load more");
        }
        assertThat(likes.findForPosts(List.of(24L), 1L).get(24L)).isEqualTo(new PostLikeVM(24L, 1, true));
        assertThat(likes.findForPosts(List.of(24L), 2L).get(24L)).isEqualTo(new PostLikeVM(24L, 1, false));
        for (int i = 0; i < 2; i++) {
            var result = mvc.post()
                    .uri("/posts/24/unlike")
                    .with(user(author))
                    .with(csrf())
                    .exchange();
            assertThat(result).hasStatusOk();
            assertThat(result.getResponse().getContentAsString())
                    .contains("aria-label=\"Like\"", "♡")
                    .doesNotContain("like-count");
        }
    }

    @Test
    void shouldKeepOtherUsersLikesAndPersistStateAcrossFeedRequests() throws UnsupportedEncodingException {
        likes.setLiked(24L, 1L, true);
        likes.setLiked(24L, 2L, true);
        assertThat(likes.findForPosts(List.of(24L), null).get(24L).count()).isEqualTo(2);
        var result = mvc.get().uri("/").with(user(reader)).exchange();
        assertThat(result).hasStatusOk();
        assertThat(result.getResponse().getContentAsString()).contains(">2</span>", "hx-post=\"/posts/24/unlike\"");
        assertThat(mvc.post().uri("/posts/24/unlike").with(user(author)).with(csrf()))
                .hasStatusOk();
        assertThat(likes.findForPosts(List.of(24L), 2L).get(24L)).isEqualTo(new PostLikeVM(24L, 1, true));
        var reload = mvc.get().uri("/").with(user(reader)).exchange();
        assertThat(reload.getResponse().getContentAsString()).contains("hx-post=\"/posts/24/unlike\"");
    }

    @Test
    void shouldShowCountsWithoutControlsToAnonymousVisitorsIncludingLoadedPosts() throws UnsupportedEncodingException {
        likes.setLiked(15L, 1L, true);
        var home = mvc.get().uri("/").exchange();
        assertThat(home.getResponse().getContentAsString())
                .doesNotContain("like-count")
                .doesNotContain("hx-post=", "aria-label=\"Like\"", "♡", "aria-label=\"Unlike\"", "♥");
        var more = mvc.get().uri("/posts?page=2").exchange();
        assertThat(more.getResponse().getContentAsString())
                .contains("class=\"like-count\">1</span>")
                .doesNotContain("hx-post=");
        var authenticated = mvc.get().uri("/posts?page=2").with(user(author)).exchange();
        assertThat(authenticated.getResponse().getContentAsString()).contains("hx-post=\"/posts/15/unlike\"");
    }

    @Test
    void shouldReturnToOriginatingPageWhenSessionExpires() {
        for (String action : List.of("like", "unlike")) {
            var anonymous = mvc.post().uri("/posts/24/" + action).with(csrf()).exchange();
            assertThat(anonymous).hasStatus(HttpStatus.FOUND);
            var expired = mvc.post()
                    .uri("/posts/24/" + action)
                    .header("HX-Request", "true")
                    .header("HX-Current-URL", "http://localhost/?feed=latest")
                    .param("_csrf", "expired-token")
                    .exchange();
            assertThat(expired).hasStatus(HttpStatus.FORBIDDEN);
            assertThat(expired.getResponse().getHeader("HX-Redirect")).isEqualTo("/?feed=latest");
            var noSession = mvc.post()
                    .uri("/posts/24/" + action)
                    .with(csrf())
                    .header("HX-Request", "true")
                    .header("HX-Current-URL", "http://localhost/")
                    .exchange();
            assertThat(noSession).hasStatus(HttpStatus.UNAUTHORIZED);
            assertThat(noSession.getResponse().getHeader("HX-Redirect")).isEqualTo("/");
        }
        assertThat(likes.findForPosts(List.of(24L), 1L).get(24L).count()).isZero();
    }

    @Test
    void shouldRefreshWhenExpiredRequestHasNoOriginatingPage() {
        var result =
                mvc.post().uri("/posts/24/like").header("HX-Request", "true").exchange();
        assertThat(result).hasStatus(HttpStatus.FORBIDDEN);
        assertThat(result.getResponse().getHeader("HX-Refresh")).isEqualTo("true");
        assertThat(result.getResponse().getHeader("HX-Redirect")).isNull();
        assertThat(likes.findForPosts(List.of(24L), 1L).get(24L).count()).isZero();
    }

    @Test
    void shouldRejectMissingCsrfWithoutChangingLikes() {
        likes.setLiked(24L, 1L, true);
        for (String action : List.of("like", "unlike")) {
            assertThat(mvc.post().uri("/posts/24/" + action).with(user(author)).header("HX-Request", "true"))
                    .hasStatus(HttpStatus.FORBIDDEN);
        }
        assertThat(likes.findForPosts(List.of(24L), 1L).get(24L)).isEqualTo(new PostLikeVM(24L, 1, true));
    }

    @Test
    void shouldRejectMissingPostsWithoutChangingExistingLikes() {
        likes.setLiked(24L, 1L, true);
        for (String action : List.of("like", "unlike")) {
            assertThat(mvc.post()
                            .uri("/posts/999999/" + action)
                            .with(user(author))
                            .with(csrf()))
                    .hasStatus(HttpStatus.NOT_FOUND);
        }
        assertThat(likes.findForPosts(List.of(24L), 1L).get(24L)).isEqualTo(new PostLikeVM(24L, 1, true));
        assertThat(likes.findForPosts(List.of(), 1L)).isEmpty();
    }

    @Test
    void shouldHandleConcurrentLikesWithoutDuplicates() throws Exception {
        var start = new CountDownLatch(1);
        try (var executor = Executors.newFixedThreadPool(6)) {
            var tasks = java.util.stream.IntStream.range(0, 6)
                    .mapToObj(i -> executor.submit(() -> {
                        start.await();
                        return likes.setLiked(24L, i % 2 == 0 ? 1L : 2L, true);
                    }))
                    .toList();
            start.countDown();
            for (var task : tasks) {
                assertThat(task.get(10, TimeUnit.SECONDS).liked()).isTrue();
            }
        }
        assertThat(likes.findForPosts(List.of(24L), 1L).get(24L)).isEqualTo(new PostLikeVM(24L, 2, true));
    }
}
