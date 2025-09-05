package io.github.julianobrl.discordbots.framework.exceptions;

public class BotLoaderException extends RuntimeException {
    public BotLoaderException() {
        super();
    }
    public BotLoaderException(String message) {
        super(message);
    }
    public BotLoaderException(String message, Throwable cause) {
        super(message, cause);
    }
    public BotLoaderException(Throwable cause) {
        super(cause);
    }
    protected BotLoaderException(String message, Throwable cause,
                                 boolean enableSuppression,
                                 boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
