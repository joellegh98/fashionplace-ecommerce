package com.fashionplace.service;

import com.fashionplace.model.ActivityLog;
import com.fashionplace.repository.ActivityLogRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Records key user actions (orders, product changes, admin moderation) to the
 * {@link ActivityLog} table and reads them back for the admin area.
 */
@Service
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;

    public ActivityLogService(ActivityLogRepository activityLogRepository) {
        this.activityLogRepository = activityLogRepository;
    }

    /**
     * Maps a completed request to a human-friendly action label, or {@code null} if the
     * request is not a key action worth logging. Only state-changing {@code POST} requests
     * to known endpoints are logged; everything else is ignored.
     *
     * @param method the HTTP method
     * @param path   the request path (without query string)
     * @return the action label, or {@code null} if the request should not be logged
     */
    public String actionFor(String method, String path) {
        if (!"POST".equalsIgnoreCase(method) || path == null) {
            return null;
        }
        if (path.equals("/sell")) {
            return "Created product";
        }
        if (path.equals("/checkout")) {
            return "Placed order";
        }
        if (path.equals("/support")) {
            return "Contacted support";
        }
        if (path.matches("/product/\\d+/edit")) {
            return "Updated product";
        }
        if (path.matches("/product/\\d+/delete")) {
            return "Deleted product";
        }
        if (path.matches("/admin/products/\\d+/delete")) {
            return "Admin deleted product";
        }
        if (path.matches("/admin/products/\\d+/flag")) {
            return "Admin flagged product";
        }
        if (path.matches("/admin/products/\\d+/unflag")) {
            return "Admin unflagged product";
        }
        if (path.matches("/admin/users/\\d+/disable")) {
            return "Admin disabled user";
        }
        if (path.matches("/admin/users/\\d+/enable")) {
            return "Admin enabled user";
        }
        if (path.matches("/admin/support/\\d+/reply")) {
            return "Admin replied to support";
        }
        if (path.matches("/admin/support/\\d+/close")) {
            return "Admin closed support thread";
        }
        return null;
    }

    /**
     * Persists an activity entry.
     *
     * @param username the actor's username (or {@code anonymous})
     * @param action   the human-friendly action description
     * @param method   the HTTP method
     * @param path     the request path
     */
    public void record(String username, String action, String method, String path) {
        activityLogRepository.save(new ActivityLog(username, action, method, path));
    }

    /**
     * @return the most recent activity entries, newest first
     */
    public List<ActivityLog> recentActivity() {
        return activityLogRepository.findTop100ByOrderByIdDesc();
    }
}
