package org.openCDI.exceptions.validation;

public class ClassIsAbstractException extends ComponentValidationException {
    public ClassIsAbstractException(Throwable cause) {
        super(cause);
    }

    public ClassIsAbstractException(String message) {
        super(message);
    }
}
