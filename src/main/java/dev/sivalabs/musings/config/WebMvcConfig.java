package dev.sivalabs.musings.config;

import gg.jte.TemplateEngine;
import org.springframework.boot.webmvc.autoconfigure.error.ErrorViewResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login").setViewName("login");
    }

    /**
     * Resolves {@code error/<status>} JTE templates for errors handled by Spring Boot's error controller,
     * since JTE does not provide a template availability provider.
     */
    @Bean
    ErrorViewResolver jteErrorViewResolver(TemplateEngine templateEngine) {
        return (request, status, model) -> {
            String viewName = "error/" + status.value();
            return templateEngine.hasTemplate(viewName + ".jte") ? new ModelAndView(viewName, model, status) : null;
        };
    }
}
