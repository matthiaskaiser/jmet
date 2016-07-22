package de.codewhite.jmet.exceptions;

/**
 * Created by kaimatt.
 */
public class SendException extends Exception {
    public SendException() {
        super();
    }

    public SendException(String message) {
        super(message);
    }

    public SendException(String message, Throwable cause) {
        super(message, cause);
    }

    public SendException(Throwable cause) {
        super(cause);
    }

    protected SendException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
