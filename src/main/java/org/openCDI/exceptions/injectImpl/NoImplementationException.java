package org.openCDI.exceptions.injectImpl;

public class NoImplementationException extends ImplementationException{

    public NoImplementationException(String message) {
        super(message);
    }

    public NoImplementationException(Throwable cause) {
        super(cause);
    }
}
