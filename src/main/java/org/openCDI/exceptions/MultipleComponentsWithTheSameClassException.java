package org.openCDI.exceptions;

public class MultipleComponentsWithTheSameClassException extends RuntimeException {

    public MultipleComponentsWithTheSameClassException(String message) {
        super(message);
    }

    public MultipleComponentsWithTheSameClassException(Throwable cause) {
        super(cause);
    }
}
