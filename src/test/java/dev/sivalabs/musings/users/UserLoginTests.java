package dev.sivalabs.musings.users;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;

import dev.sivalabs.musings.BaseIT;
import java.io.UnsupportedEncodingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.support.TransactionTemplate;

class UserLoginTests extends BaseIT {
    private static final String EMAIL = "login-test@example.com";
    private static final String PASSWORD = "secret123";

    private final JdbcTemplate jdbc;
    private final PasswordEncoder passwordEncoder;
    private final TransactionTemplate tx;

    @Autowired
    UserLoginTests(JdbcTemplate jdbc, PasswordEncoder passwordEncoder, TransactionTemplate tx) {
        this.jdbc = jdbc;
        this.passwordEncoder = passwordEncoder;
        this.tx = tx;
    }

    @BeforeEach
    void setUp() {
        // auto-commit is disabled, so writes must run inside a transaction to be visible to the app
        tx.executeWithoutResult(status -> {
            jdbc.update("delete from users where email = ?", EMAIL);
            jdbc.update(
                    "insert into users(id, email, password, name, role, created_at) values (?, ?, ?, ?, ?, CURRENT_TIMESTAMP)",
                    1000L,
                    EMAIL,
                    passwordEncoder.encode(PASSWORD),
                    "Login Tester",
                    Role.ROLE_USER.name());
        });
    }

    @Test
    void shouldRenderLoginPage() throws UnsupportedEncodingException {
        var result = mvc.get().uri("/login").exchange();
        assertThat(result).hasStatusOk().hasViewName("login");
        assertThat(result.getResponse().getContentAsString())
                .contains("Welcome back", "name=\"username\"", "name=\"password\"", "name=\"_csrf\"")
                .doesNotContain("Invalid email or password.");
    }

    @Test
    void shouldLoginWithValidCredentials() throws UnsupportedEncodingException {
        var result = mvc.post()
                .uri("/login")
                .with(csrf())
                .param("username", EMAIL)
                .param("password", PASSWORD)
                .exchange();
        assertThat(result)
                .hasStatus(HttpStatus.FOUND)
                .hasRedirectedUrl("/")
                .matches(authenticated().withUsername(EMAIL));

        var home = mvc.get()
                .uri("/")
                .session((MockHttpSession) result.getRequest().getSession())
                .exchange();
        assertThat(home).hasStatusOk();
        assertThat(home.getResponse().getContentAsString()).contains("Login Tester", "Post a message", "Logout");
    }

    @Test
    void shouldRejectInvalidPassword() {
        var result = mvc.post()
                .uri("/login")
                .with(csrf())
                .param("username", EMAIL)
                .param("password", "wrong-password")
                .exchange();
        assertThat(result)
                .hasStatus(HttpStatus.FOUND)
                .hasRedirectedUrl("/login?error")
                .matches(unauthenticated());
    }

    @Test
    void shouldRejectUnknownUser() {
        var result = mvc.post()
                .uri("/login")
                .with(csrf())
                .param("username", "nobody@example.com")
                .param("password", PASSWORD)
                .exchange();
        assertThat(result)
                .hasStatus(HttpStatus.FOUND)
                .hasRedirectedUrl("/login?error")
                .matches(unauthenticated());
    }

    @Test
    void shouldShowErrorMessageAfterFailedLogin() throws UnsupportedEncodingException {
        var result = mvc.get().uri("/login").param("error", "").exchange();
        assertThat(result).hasStatusOk();
        assertThat(result.getResponse().getContentAsString()).contains("Invalid email or password.");
    }

    @Test
    void shouldRejectLoginWithoutCsrfToken() {
        var result = mvc.post()
                .uri("/login")
                .param("username", EMAIL)
                .param("password", PASSWORD)
                .exchange();
        assertThat(result).hasStatus(HttpStatus.FORBIDDEN).matches(unauthenticated());
    }

    @Test
    void shouldRedirectAnonymousUserToLoginForProtectedResource() {
        var result = mvc.get().uri("/posts/new").exchange();
        assertThat(result).hasStatus(HttpStatus.FOUND).hasRedirectedUrl("/login");
    }

    @Test
    void shouldLogoutAndShowHomePage() {
        var login = mvc.post()
                .uri("/login")
                .with(csrf())
                .param("username", EMAIL)
                .param("password", PASSWORD)
                .exchange();
        assertThat(login).matches(authenticated());

        var logout = mvc.post()
                .uri("/logout")
                .session((MockHttpSession) login.getRequest().getSession())
                .with(csrf())
                .exchange();
        assertThat(logout).hasStatus(HttpStatus.FOUND).hasRedirectedUrl("/").matches(unauthenticated());
    }
}
