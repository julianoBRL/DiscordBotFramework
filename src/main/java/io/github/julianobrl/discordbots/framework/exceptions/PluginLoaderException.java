package io.github.julianobrl.discordbots.framework.exceptions;

public class PluginLoaderException extends RuntimeException {
    public PluginLoaderException() {
        super();
    }
    public PluginLoaderException(String message) {
        super(message);
    }
    public PluginLoaderException(String message, Throwable cause) {
        super(message, cause);
    }
    public PluginLoaderException(Throwable cause) {
        super(cause);
    }
    protected PluginLoaderException(String message, Throwable cause,
                                    boolean enableSuppression,
                                    boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
