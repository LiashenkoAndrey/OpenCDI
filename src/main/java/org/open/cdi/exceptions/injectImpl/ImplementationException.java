package org.open.cdi.exceptions.injectImpl;

public abstract class ImplementationException extends RuntimeException {

    public ImplementationException(String message) {
        super(message);
    }

    public ImplementationException(Throwable cause) {
        super(cause);
    }
}
