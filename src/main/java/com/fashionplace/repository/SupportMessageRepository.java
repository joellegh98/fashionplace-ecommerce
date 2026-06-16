package com.fashionplace.repository;

import com.fashionplace.model.SupportMessage;
import com.fashionplace.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for {@link SupportMessage} entities. Provides CRUD methods for free.
 */
@Repository
public interface SupportMessageRepository extends JpaRepository<SupportMessage, Long> {

    /**
     * Returns all messages belonging to the given user, oldest first (conversation order).
     *
     * @param user the customer whose messages to fetch
     * @return that user's support messages
     */
    List<SupportMessage> findByUserOrderByIdAsc(User user);

    /**
     * Returns all messages with the given status, newest first (for the admin inbox).
     *
     * @param status the conversation status, e.g. {@code OPEN} or {@code CLOSED}
     * @return matching messages
     */
    List<SupportMessage> findByStatusOrderByIdDesc(String status);

    /**
     * Returns all messages in one conversation (same user and subject), oldest first.
     *
     * @param user    the customer the conversation belongs to
     * @param subject the shared subject line
     * @return the conversation's messages in order
     */
    List<SupportMessage> findByUserAndSubjectOrderByIdAsc(User user, String subject);

    /**
     * Returns every message, oldest first, for grouping into the admin inbox.
     *
     * @return all support messages in id order
     */
    List<SupportMessage> findAllByOrderByIdAsc();
}
