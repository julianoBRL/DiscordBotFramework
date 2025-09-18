package io.github.julianobrl.discordbots.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public class VersionException extends RuntimeException {

    @Getter
    private HttpStatus status;

    public VersionException() {
        super();
    }
    public VersionException(String message) {
        super(message);
    }
    public VersionException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
    public VersionException(String message, Throwable cause) {
        super(message, cause);
    }
    public VersionException(Throwable cause) {
        super(cause);
    }
    protected VersionException(String message, Throwable cause,
                               boolean enableSuppression,
                               boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
