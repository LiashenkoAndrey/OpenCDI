package org.open.cdi.exceptions.validation;

public class ComponentValidationException extends RuntimeException {

    public ComponentValidationException(Throwable cause) {
        super(cause);
    }

    public ComponentValidationException(String message) {
        super(message);
    }
}
