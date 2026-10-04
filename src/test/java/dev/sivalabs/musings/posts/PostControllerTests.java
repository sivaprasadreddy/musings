package dev.sivalabs.musings.posts;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import dev.sivalabs.musings.BaseIT;
import dev.sivalabs.musings.shared.PagedResult;
import dev.sivalabs.musings.users.Role;
import dev.sivalabs.musings.users.SecurityUser;
import java.io.UnsupportedEncodingException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;

@Sql("/test-data.sql")
class PostControllerTests extends BaseIT {
    private final JdbcTemplate jdbc;
    private final PostService postService;
    private final SecurityUser principal =
            new SecurityUser(1L, "Siva", "sivaprasadreddy.k@gmail.com", "", Role.ROLE_ADMIN);

    @Autowired
    PostControllerTests(JdbcTemplate jdbc, PostService postService) {
        this.jdbc = jdbc;
        this.postService = postService;
    }

    @Test
    void shouldRenderHomeWithSeededPostsAndPostForm() throws UnsupportedEncodingException {
        var result = mvc.get().uri("/").with(user(principal)).exchange();
        assertThat(result).hasStatusOk().hasViewName("index");
        assertThat(result.getResponse().getContentAsString())
                .contains("Post a message", "Post 24", "Message 24", "name=\"_csrf\"", "Siva", "Load more")
                .doesNotContain("No messages yet");
        assertThat(result).model().hasEntrySatisfying("posts", value -> {
            var posts = (PagedResult<?>) value;
            assertThat(posts.data()).hasSize(10);
            assertThat(posts.totalElements()).isEqualTo(25);
        });
    }

    @Test
    void shouldCreatePostWithAuthenticatedAuthorAndEscapeContent() throws UnsupportedEncodingException {
        var result = mvc.post()
                .uri("/posts")
                .with(user(principal))
                .with(csrf())
                .param("title", "  First post  ")
                .param("content", "<script>alert('test')</script>\nHello")
                .param("authorId", "2")
                .exchange();
        assertThat(result).hasStatus(HttpStatus.FOUND).hasRedirectedUrl("/");
        assertThat(jdbc.queryForObject("select author_id from posts where title = ?", Long.class, "First post"))
                .isEqualTo(principal.getId());
        var posts = postService.findLatest(1);
        assertThat(posts.totalElements()).isEqualTo(26);
        assertThat(posts.data())
                .filteredOn(post -> post.title().equals("First post"))
                .singleElement()
                .satisfies(post -> {
                    assertThat(post.title()).isEqualTo("First post");
                    assertThat(post.authorName()).isEqualTo("Siva");
                    assertThat(post.createdAt()).isNotNull();
                });
        var home = mvc.get().uri("/").with(user(principal)).exchange();
        assertThat(home).hasStatusOk();
        assertThat(home.getResponse().getContentAsString())
                .contains("&lt;script&gt;")
                .doesNotContain("<script>alert");
    }

    @Test
    void shouldRejectInvalidPostAndPreserveInput() throws UnsupportedEncodingException {
        var result = mvc.post()
                .uri("/posts")
                .with(user(principal))
                .with(csrf())
                .param("title", " ")
                .param("content", "Keep this message")
                .exchange();
        assertThat(result)
                .hasStatusOk()
                .hasViewName("index")
                .model()
                .extractingBindingResult("postForm")
                .hasFieldErrors("title");
        assertThat(result.getResponse().getContentAsString()).contains("Keep this message", "Enter a title");
        assertThat(postService.findLatest(1).totalElements()).isEqualTo(25);
    }

    @Test
    void shouldRejectOversizedAndBlankContent() {
        var result = mvc.post()
                .uri("/posts")
                .with(user(principal))
                .with(csrf())
                .param("title", "x".repeat(201))
                .param("content", " ")
                .exchange();
        assertThat(result)
                .hasStatusOk()
                .model()
                .extractingBindingResult("postForm")
                .hasFieldErrors("title", "content");
        var oversized = mvc.post()
                .uri("/posts")
                .with(user(principal))
                .with(csrf())
                .param("title", "Title")
                .param("content", "x".repeat(10001))
                .exchange();
        assertThat(oversized)
                .hasStatusOk()
                .model()
                .extractingBindingResult("postForm")
                .hasFieldErrors("content");
        assertThat(postService.findLatest(1).totalElements()).isEqualTo(25);
    }

    @Test
    void shouldLoadTenPostsAtATimeInDescendingOrder() throws UnsupportedEncodingException {
        var home = mvc.get().uri("/").with(user(principal)).exchange();
        assertThat(home).hasStatusOk();
        assertThat(home)
                .model()
                .hasEntrySatisfying(
                        "posts",
                        value -> assertThat(((PagedResult<?>) value).data()).hasSize(10));
        assertThat(postService.findLatest(1).data())
                .extracting(PostVM::id)
                .containsExactly(24L, 25L, 23L, 22L, 21L, 20L, 19L, 18L, 17L, 16L);
        assertThat(home.getResponse().getContentAsString()).contains("hx-get=\"/posts?page=2\"");

        var more =
                mvc.get().uri("/posts").param("page", "2").with(user(principal)).exchange();
        assertThat(more).hasStatusOk().hasViewName("posts/feed :: feed");
        assertThat(more.getResponse().getContentAsString())
                .contains("hx-get=\"/posts?page=3\"", "Post 15", "Post 6")
                .doesNotContain("<html", "Post a message", "Post 16", "Post 5</h3>");
        assertThat(postService.findLatest(2).data())
                .extracting(PostVM::id)
                .containsExactly(15L, 14L, 13L, 12L, 11L, 10L, 9L, 8L, 7L, 6L);

        var last =
                mvc.get().uri("/posts").param("page", "3").with(user(principal)).exchange();
        assertThat(last).hasStatusOk();
        assertThat(postService.findLatest(3).data()).extracting(PostVM::id).containsExactly(5L, 4L, 3L, 2L, 1L);
        assertThat(last.getResponse().getContentAsString())
                .contains("Post 5", "Post 1")
                .doesNotContain("Load more");
    }

    @Test
    void shouldSortByCreationTimeBeforeId() {
        assertThat(postService.findLatest(1).data())
                .extracting(PostVM::title)
                .startsWith("Post 24", "Post 25", "Post 23");
    }

    @Test
    void shouldRequireAuthenticationAndCsrfToCreatePosts() {
        assertThat(mvc.post().uri("/posts").with(csrf()).param("title", "Title").param("content", "Message"))
                .hasStatus(HttpStatus.FOUND);
        assertThat(mvc.post()
                        .uri("/posts")
                        .with(user(principal))
                        .param("title", "Title")
                        .param("content", "Message"))
                .hasStatus(HttpStatus.FORBIDDEN);
        assertThat(postService.findLatest(1).totalElements()).isEqualTo(25);
    }
}
