package com.fashionplace.session;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Session-scoped store for the user's recent Browse keyword searches.
 *
 * <p>One instance exists per HTTP session. Must implement {@link Serializable} so the
 * session can be persisted if Spring Session JDBC is enabled later.</p>
 */
public class RecentSearchBean implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Maximum number of recent search terms kept in memory. */
    public static final int MAX_RECENT = 5;

    private final List<String> recentSearches = new ArrayList<>();

    /**
     * Records a search keyword. Blank terms are ignored. Duplicates are moved to the
     * front; the list is capped at {@link #MAX_RECENT} entries.
     *
     * @param keyword the search text the user submitted
     */
    public synchronized void addSearch(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return;
        }
        String term = keyword.trim();
        recentSearches.remove(term);
        recentSearches.add(0, term);
        if (recentSearches.size() > MAX_RECENT) {
            recentSearches.remove(recentSearches.size() - 1);
        }
    }

    /**
     * Returns the recent search terms, most recent first.
     *
     * @return a snapshot of recent searches
     */
    public synchronized List<String> getRecentSearches() {
        return List.copyOf(recentSearches);
    }
}
