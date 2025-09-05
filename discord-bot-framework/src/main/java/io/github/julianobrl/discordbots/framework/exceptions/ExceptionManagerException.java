package io.github.julianobrl.discordbots.framework.exceptions;

public class ExceptionManagerException extends RuntimeException {
    public ExceptionManagerException() {
        super();
    }
    public ExceptionManagerException(String message) {
        super(message);
    }
    public ExceptionManagerException(String message, Throwable cause) {
        super(message, cause);
    }
    public ExceptionManagerException(Throwable cause) {
        super(cause);
    }
    protected ExceptionManagerException(String message, Throwable cause,
                                        boolean enableSuppression,
                                        boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
