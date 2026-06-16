package com.fashionplace.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Form backing object for submitting a customer-support message from the {@code /support} page.
 */
public class SupportForm {

    @NotBlank(message = "Please enter a subject.")
    @Size(max = 150, message = "Subject must be at most 150 characters.")
    private String subject;

    @NotBlank(message = "Please enter a message.")
    private String body;

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
