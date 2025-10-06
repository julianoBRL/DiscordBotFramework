package io.github.julianobrl.discordbots.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public class AppException extends RuntimeException {

    @Getter
    private HttpStatus status;

    public AppException() {
        super();
    }
    public AppException(String message) {
        super(message);
    }
    public AppException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
    public AppException(String message, Throwable cause) {
        super(message, cause);
    }
    public AppException(Throwable cause) {
        super(cause);
    }
    protected AppException(String message, Throwable cause,
                           boolean enableSuppression,
                           boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
