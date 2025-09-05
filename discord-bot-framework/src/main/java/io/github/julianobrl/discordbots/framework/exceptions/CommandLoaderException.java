package io.github.julianobrl.discordbots.framework.exceptions;

public class CommandLoaderException extends RuntimeException {
    public CommandLoaderException() {
        super();
    }
    public CommandLoaderException(String message) {
        super(message);
    }
    public CommandLoaderException(String message, Throwable cause) {
        super(message, cause);
    }
    public CommandLoaderException(Throwable cause) {
        super(cause);
    }
    protected CommandLoaderException(String message, Throwable cause,
                                     boolean enableSuppression,
                                     boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
