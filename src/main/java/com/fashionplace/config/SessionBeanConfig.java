package com.fashionplace.config;

import com.fashionplace.session.RecentSearchBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.SessionScope;

/**
 * Declares session-scoped beans used across the application.
 */
@Configuration
public class SessionBeanConfig {

    /**
     * Per-session store of recent Browse search keywords.
     */
    @Bean
    @SessionScope
    public RecentSearchBean recentSearchBean() {
        return new RecentSearchBean();
    }
}
