package ru.practicum.shareit.exception;

import javax.validation.constraints.Pattern;

public class ValidationException extends Exception {
    public ValidationException(final String message) {
        super(message);
    }

    public ValidationException() {
    }

}
