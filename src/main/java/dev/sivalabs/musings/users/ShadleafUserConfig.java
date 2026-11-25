package dev.sivalabs.musings.users;

import io.github.wimdeblauwe.shadleaf.security.CurrentUserResolver;
import io.github.wimdeblauwe.shadleaf.security.DefaultCurrentUserResolver;
import io.github.wimdeblauwe.shadleaf.security.ShadleafUser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class ShadleafUserConfig {

    /** Shows the user's display name (not the email username) in Shadleaf's user menu. */
    @Bean
    CurrentUserResolver currentUserResolver() {
        CurrentUserResolver fallback = new DefaultCurrentUserResolver();
        return authentication -> authentication.getPrincipal() instanceof SecurityUser user
                ? ShadleafUser.of(user.getName(), user.getUsername(), user.getUsername(), null)
                : fallback.resolve(authentication);
    }
}
