package com.fashionplace.service;

import com.fashionplace.model.SupportMessage;
import com.fashionplace.model.User;
import com.fashionplace.repository.SupportMessageRepository;
import com.fashionplace.web.SupportForm;
import com.fashionplace.web.SupportThread;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Business logic for customer support messaging. Uses {@link CurrentUserProvider} for the
 * (temporary, fake) current user until real authentication arrives in Phase 9.
 */
@Service
public class SupportService {

    private final SupportMessageRepository supportMessageRepository;
    private final CurrentUserProvider currentUserProvider;

    public SupportService(SupportMessageRepository supportMessageRepository,
                          CurrentUserProvider currentUserProvider) {
        this.supportMessageRepository = supportMessageRepository;
        this.currentUserProvider = currentUserProvider;
    }

    /**
     * Saves a new support message from the current user. The message is attributed to the
     * {@code USER} sender and the conversation starts {@code OPEN}.
     *
     * @param form the submitted subject and body
     * @return the saved message
     */
    public SupportMessage submitForCurrentUser(SupportForm form) {
        User user = currentUserProvider.getCurrentUser();
        SupportMessage message = new SupportMessage(
                user,
                form.getSubject().trim(),
                form.getBody().trim(),
                SupportMessage.SENDER_USER,
                SupportMessage.STATUS_OPEN);
        return supportMessageRepository.save(message);
    }

    /**
     * Adds a reply from the current user to one of their own open conversations. The reply is
     * attributed to the {@code USER} sender and keeps the conversation {@code OPEN}.
     *
     * @param messageId any message id within the target conversation
     * @param body      the reply text
     * @throws java.util.NoSuchElementException if no message has the given id
     * @throws IllegalArgumentException         if the reply body is blank
     * @throws IllegalStateException            if the conversation isn't the user's own, or is closed
     */
    @Transactional
    public void replyAsCurrentUser(Long messageId, String body) {
        if (body == null || body.isBlank()) {
            throw new IllegalArgumentException("Reply cannot be empty.");
        }
        User user = currentUserProvider.getCurrentUser();
        SupportMessage source = supportMessageRepository.findById(messageId).orElseThrow();
        if (source.getUser() == null || !source.getUser().getId().equals(user.getId())) {
            throw new IllegalStateException("You can only reply to your own conversations.");
        }

        List<SupportMessage> thread =
                supportMessageRepository.findByUserAndSubjectOrderByIdAsc(user, source.getSubject());
        boolean closed = !thread.isEmpty()
                && SupportMessage.STATUS_CLOSED.equals(thread.get(thread.size() - 1).getStatus());
        if (closed) {
            throw new IllegalStateException("This conversation is closed.");
        }

        supportMessageRepository.save(new SupportMessage(
                user, source.getSubject(), body.trim(),
                SupportMessage.SENDER_USER, SupportMessage.STATUS_OPEN));
    }

    /**
     * Returns the current user's support conversations, grouped by subject. Within a thread
     * messages are ordered oldest first; threads are ordered with the most recently updated
     * first.
     *
     * @return the current user's support threads
     */
    public List<SupportThread> threadsForCurrentUser() {
        List<SupportMessage> messages =
                supportMessageRepository.findByUserOrderByIdAsc(currentUserProvider.getCurrentUser());
        // Single user, so group by subject alone.
        return groupIntoThreads(messages, SupportMessage::getSubject);
    }

    /**
     * Returns every support conversation across all users, for the admin inbox. Threads are
     * grouped by user and subject, messages oldest first, most recently active thread first.
     *
     * @return all support threads
     */
    public List<SupportThread> allThreads() {
        List<SupportMessage> messages = supportMessageRepository.findAllByOrderByIdAsc();
        // Different users may share a subject, so the grouping key includes the user id.
        return groupIntoThreads(messages,
                message -> message.getUser().getId() + "::" + message.getSubject());
    }

    /**
     * Adds a support-team reply to the conversation the given message belongs to. The reply is
     * attributed to the {@code SUPPORT} sender and re-opens the conversation if it was closed.
     *
     * @param messageId any message id within the target conversation
     * @param body      the reply text
     * @throws java.util.NoSuchElementException if no message has the given id
     * @throws IllegalArgumentException         if the reply body is blank
     */
    @Transactional
    public void replyToThread(Long messageId, String body) {
        if (body == null || body.isBlank()) {
            throw new IllegalArgumentException("Reply cannot be empty.");
        }
        SupportMessage source = supportMessageRepository.findById(messageId).orElseThrow();
        User user = source.getUser();
        String subject = source.getSubject();

        // Re-open the existing messages so the whole thread reads as OPEN again.
        List<SupportMessage> existing =
                supportMessageRepository.findByUserAndSubjectOrderByIdAsc(user, subject);
        for (SupportMessage message : existing) {
            message.setStatus(SupportMessage.STATUS_OPEN);
        }
        supportMessageRepository.saveAll(existing);

        supportMessageRepository.save(new SupportMessage(
                user, subject, body.trim(),
                SupportMessage.SENDER_SUPPORT, SupportMessage.STATUS_OPEN));
    }

    /**
     * Marks every message in the conversation the given message belongs to as {@code CLOSED}.
     *
     * @param messageId any message id within the target conversation
     * @throws java.util.NoSuchElementException if no message has the given id
     */
    @Transactional
    public void closeThread(Long messageId) {
        SupportMessage source = supportMessageRepository.findById(messageId).orElseThrow();
        List<SupportMessage> thread = supportMessageRepository
                .findByUserAndSubjectOrderByIdAsc(source.getUser(), source.getSubject());
        for (SupportMessage message : thread) {
            message.setStatus(SupportMessage.STATUS_CLOSED);
        }
        supportMessageRepository.saveAll(thread);
    }

    /**
     * Groups messages into threads using the given key, preserving encounter order within each
     * thread (oldest first) and sorting threads by most recent activity first.
     */
    private List<SupportThread> groupIntoThreads(List<SupportMessage> messages,
                                                 Function<SupportMessage, String> keyFn) {
        Map<String, List<SupportMessage>> byKey = new LinkedHashMap<>();
        for (SupportMessage message : messages) {
            byKey.computeIfAbsent(keyFn.apply(message), key -> new ArrayList<>()).add(message);
        }

        List<SupportThread> threads = new ArrayList<>();
        for (List<SupportMessage> group : byKey.values()) {
            threads.add(new SupportThread(group.get(0).getSubject(), group));
        }
        threads.sort((a, b) -> Long.compare(lastId(b), lastId(a)));
        return threads;
    }

    /** @return the id of the most recent message in a thread (0 when empty) */
    private long lastId(SupportThread thread) {
        List<SupportMessage> messages = thread.getMessages();
        if (messages.isEmpty()) {
            return 0L;
        }
        Long id = messages.get(messages.size() - 1).getId();
        return id == null ? 0L : id;
    }
}
