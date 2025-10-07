package io.github.julianobrl.discordbots.framework.exceptions;

public class ListenerLoaderException extends RuntimeException {
    public ListenerLoaderException() {
        super();
    }
    public ListenerLoaderException(String message) {
        super(message);
    }
    public ListenerLoaderException(String message, Throwable cause) {
        super(message, cause);
    }
    public ListenerLoaderException(Throwable cause) {
        super(cause);
    }
    protected ListenerLoaderException(String message, Throwable cause,
                                      boolean enableSuppression,
                                      boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
