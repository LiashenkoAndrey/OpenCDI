package org.openCDI.exceptions.validation;

public class MultipleAnnotationException extends ComponentValidationException {

    public MultipleAnnotationException(Throwable cause) {
        super(cause);
    }

    public MultipleAnnotationException(String message) {
        super(message);
    }
}
