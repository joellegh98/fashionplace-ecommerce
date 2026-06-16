package com.fashionplace.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Registers Spring MVC interceptors. Currently wires the {@link ActivityLogInterceptor}
 * into the request pipeline so key actions are logged for the admin activity feed.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final ActivityLogInterceptor activityLogInterceptor;

    public WebConfig(ActivityLogInterceptor activityLogInterceptor) {
        this.activityLogInterceptor = activityLogInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(activityLogInterceptor);
    }
}
