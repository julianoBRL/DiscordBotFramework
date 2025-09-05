package io.github.julianobrl.discordbots.framework.exceptions;

public class FieldParameterInjectionException extends RuntimeException {
    public FieldParameterInjectionException() {
        super();
    }
    public FieldParameterInjectionException(String message) {
        super(message);
    }
    public FieldParameterInjectionException(String message, Throwable cause) {
        super(message, cause);
    }
    public FieldParameterInjectionException(Throwable cause) {
        super(cause);
    }
    protected FieldParameterInjectionException(String message, Throwable cause,
                                               boolean enableSuppression,
                                               boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
