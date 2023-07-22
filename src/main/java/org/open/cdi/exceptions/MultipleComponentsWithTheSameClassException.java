package org.open.cdi.exceptions;

public class MultipleComponentsWithTheSameClassException extends RuntimeException {

    public MultipleComponentsWithTheSameClassException(String message) {
        super(message);
    }

    public MultipleComponentsWithTheSameClassException(Throwable cause) {
        super(cause);
    }
}
