package com.fashionplace.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Catches uncaught exceptions from controllers and returns a friendly response instead of
 * a raw stack trace. HTML requests get the shared {@code error} view; API requests get JSON.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Missing entity (e.g. {@code findById(...).orElseThrow()}).
     *
     * @param ex      the exception thrown when an optional was empty
     * @param request the current HTTP request
     * @return JSON error body for API requests, or the {@code error} view name for HTML
     */
    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Object handleNotFound(NoSuchElementException ex, HttpServletRequest request) {
        if (wantsJson(request)) {
            return jsonResponse(HttpStatus.NOT_FOUND, "The requested resource was not found.");
        }
        return "error";
    }

    /**
     * Authenticated user tried to access or modify a resource they do not own.
     *
     * @param ex      the access-denied exception
     * @param request the current HTTP request
     * @return JSON error body for API requests, or the {@code error} view name for HTML
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Object handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        if (wantsJson(request)) {
            return jsonResponse(HttpStatus.FORBIDDEN, ex.getMessage());
        }
        return "error";
    }

    /**
     * Invalid input or business-rule violation (e.g. blank reply, closed conversation).
     *
     * @param ex      the illegal-argument or illegal-state exception
     * @param request the current HTTP request
     * @return JSON error body for API requests, or the {@code error} view name for HTML
     */
    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Object handleBadRequest(RuntimeException ex, HttpServletRequest request) {
        if (wantsJson(request)) {
            return jsonResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
        return "error";
    }

    /**
     * Validation failures on {@code @RequestBody @Valid} payloads (REST-style endpoints).
     *
     * @param ex the binding validation exception
     * @return a JSON map with {@code error} and per-field {@code fields} messages
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> fieldErrors.put(error.getField(), error.getDefaultMessage()));
        return Map.of("error", "Validation failed.", "fields", fieldErrors);
    }

    /**
     * Fallback for anything not handled elsewhere.
     *
     * @param ex      the unexpected exception
     * @param request the current HTTP request
     * @return JSON error body for API requests, or the {@code error} view name for HTML
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Object handleUnexpected(Exception ex, HttpServletRequest request) {
        if (wantsJson(request)) {
            return jsonResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An error has occurred.");
        }
        return "error";
    }

    private ResponseEntity<Map<String, String>> jsonResponse(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(Map.of("error", message));
    }

    /** @return {@code true} when the client expects JSON (API path or Accept header) */
    private boolean wantsJson(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String contextPath = request.getContextPath();
        if (uri != null && uri.startsWith(contextPath + "/api/")) {
            return true;
        }
        String accept = request.getHeader("Accept");
        return accept != null && accept.contains("application/json");
    }
}
