package com.fashionplace.dto;

import com.fashionplace.model.SupportMessage;
import com.fashionplace.model.User;

import java.time.LocalDateTime;
import java.util.List;

/**
 * A support conversation: the messages sharing one subject for a single user, in order.
 *
 * <p>A thread is "a message and its replies": the user's opening message followed by any
 * support replies. Its status is taken from the most recent message.</p>
 */
public class SupportThreadDto {

    private final String subject;
    private final List<SupportMessage> messages;

    /**
     * @param subject  the shared subject line
     * @param messages the conversation's messages, oldest first
     */
    public SupportThreadDto(String subject, List<SupportMessage> messages) {
        this.subject = subject;
        this.messages = messages;
    }

    /** @return the conversation subject */
    public String getSubject() {
        return subject;
    }

    /**
     * @return the id of the thread's opening (oldest) message, used as a stable handle for
     *         admin reply/close actions, or {@code null} if the thread is empty
     */
    public Long getId() {
        return messages.isEmpty() ? null : messages.get(0).getId();
    }

    /** @return the customer this conversation belongs to, or {@code null} if empty */
    public User getUser() {
        return messages.isEmpty() ? null : messages.get(0).getUser();
    }

    /** @return the conversation's messages, oldest first */
    public List<SupportMessage> getMessages() {
        return messages;
    }

    /** @return the status of the most recent message in the thread */
    public String getStatus() {
        if (messages.isEmpty()) {
            return SupportMessage.STATUS_OPEN;
        }
        return messages.get(messages.size() - 1).getStatus();
    }

    /** @return {@code true} if the thread is still open */
    public boolean isOpen() {
        return SupportMessage.STATUS_OPEN.equals(getStatus());
    }

    /** @return when the latest message in the thread was created */
    public LocalDateTime getLastUpdated() {
        if (messages.isEmpty()) {
            return null;
        }
        return messages.get(messages.size() - 1).getCreatedAt();
    }
}
