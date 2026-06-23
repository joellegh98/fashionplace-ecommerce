package com.fashionplace.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Validates DTO instances built inside the service layer (not bound from HTTP forms).
 */
@Component
public class DtoValidator {

    private final Validator validator;

    public DtoValidator(Validator validator) {
        this.validator = validator;
    }

    /**
     * Validates the given DTO and returns it when valid.
     *
     * @param <T> the DTO type
     * @param dto the instance to validate
     * @return the same {@code dto} when validation succeeds
     * @throws IllegalStateException when Bean Validation reports constraint violations
     */
    public <T> T requireValid(T dto) {
        Set<ConstraintViolation<T>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            String message = violations.stream()
                    .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                    .collect(Collectors.joining("; "));
            throw new IllegalStateException("Invalid DTO: " + message);
        }
        return dto;
    }
}
