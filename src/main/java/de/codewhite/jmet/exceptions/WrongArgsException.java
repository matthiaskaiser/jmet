package de.codewhite.jmet.exceptions;

/**
 * Created by kaimatt.
 */
public class WrongArgsException extends Throwable {
    public WrongArgsException() {
        super();
    }

    public WrongArgsException(String message) {
        super(message);
    }

    public WrongArgsException(String message, Throwable cause) {
        super(message, cause);
    }

    public WrongArgsException(Throwable cause) {
        super(cause);
    }

    protected WrongArgsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
