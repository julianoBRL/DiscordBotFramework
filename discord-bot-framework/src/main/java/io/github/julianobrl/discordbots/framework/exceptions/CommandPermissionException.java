package io.github.julianobrl.discordbots.framework.exceptions;

public class CommandPermissionException extends RuntimeException {
    public CommandPermissionException() {
        super();
    }
    public CommandPermissionException(String message) {
        super(message);
    }
    public CommandPermissionException(String message, Throwable cause) {
        super(message, cause);
    }
    public CommandPermissionException(Throwable cause) {
        super(cause);
    }
    protected CommandPermissionException(String message, Throwable cause,
                                         boolean enableSuppression,
                                         boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
