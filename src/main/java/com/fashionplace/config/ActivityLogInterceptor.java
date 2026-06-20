package com.fashionplace.config;

import com.fashionplace.model.User;
import com.fashionplace.service.ActivityLogService;
import com.fashionplace.service.CurrentUserProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Logs key user actions (orders, product changes, admin moderation) to the activity log.
 *
 * <p>Runs once per request as a Spring-managed singleton. Logging happens in
 * {@link #afterCompletion} so only requests that actually completed are recorded; failed
 * requests (HTTP status &gt;= 400) are skipped. {@link ActivityLogService#actionFor} decides
 * which requests are "key actions" worth keeping.</p>
 */
@Component
public class ActivityLogInterceptor implements HandlerInterceptor {

    private final ActivityLogService activityLogService;
    private final CurrentUserProvider currentUserProvider;

    public ActivityLogInterceptor(ActivityLogService activityLogService,
                                  CurrentUserProvider currentUserProvider) {
        this.activityLogService = activityLogService;
        this.currentUserProvider = currentUserProvider;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        if (ex != null || response.getStatus() >= 400) {
            return;
        }
        String path = request.getRequestURI().substring(request.getContextPath().length());
        String action = activityLogService.actionFor(request.getMethod(), path);
        if (action == null) {
            return;
        }
        activityLogService.record(currentUsername(), action, request.getMethod(), path);
    }

    /** Resolves the signed-in user's username, or {@code anonymous} when not logged in. */
    private String currentUsername() {
        return currentUserProvider.getCurrentUserOptional()
                .map(User::getUsername)
                .orElse("anonymous");
    }
}
