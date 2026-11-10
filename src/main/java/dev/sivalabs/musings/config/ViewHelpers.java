package dev.sivalabs.musings.config;

import dev.sivalabs.musings.users.SecurityUser;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Static helpers used by JTE templates to access request-scoped state (security, CSRF) and formatting.
 */
public final class ViewHelpers {
    private static final DateTimeFormatter POST_DATE_FORMAT =
            DateTimeFormatter.ofPattern("d MMM yyyy, h:mm a z", Locale.ENGLISH).withZone(ZoneOffset.UTC);

    private ViewHelpers() {}

    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);
    }

    public static String currentUserName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof SecurityUser user) {
            return user.getName();
        }
        return authentication == null ? "" : authentication.getName();
    }

    public static CsrfToken csrfToken() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return null;
        }
        Object token = attributes.getAttribute(CsrfToken.class.getName(), RequestAttributes.SCOPE_REQUEST);
        return token instanceof CsrfToken csrfToken ? csrfToken : null;
    }

    public static boolean hasRequestParam(String name) {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        return attributes instanceof ServletRequestAttributes servletAttributes
                && servletAttributes.getRequest().getParameter(name) != null;
    }

    public static String formatDateTime(Instant instant) {
        return instant == null ? "" : POST_DATE_FORMAT.format(instant);
    }
}
