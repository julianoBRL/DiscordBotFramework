package io.github.julianobrl.discordbots.framework.exceptions;

public class ConfigLoaderException extends RuntimeException {
    public ConfigLoaderException() {
        super();
    }
    public ConfigLoaderException(String message) {
        super(message);
    }
    public ConfigLoaderException(String message, Throwable cause) {
        super(message, cause);
    }
    public ConfigLoaderException(Throwable cause) {
        super(cause);
    }
    protected ConfigLoaderException(String message, Throwable cause,
                               boolean enableSuppression,
                               boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
