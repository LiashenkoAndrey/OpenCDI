package org.open.cdi.exceptions.injectImpl;

public class MultipleImplementationException extends ImplementationException {


    public MultipleImplementationException(String message) {
        super(message);
    }

    public MultipleImplementationException(Throwable cause) {
        super(cause);
    }
}
