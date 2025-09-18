package io.github.julianobrl.discordbots.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public class FileException extends RuntimeException {

    @Getter
    private HttpStatus status;

    public FileException() {
        super();
    }
    public FileException(String message) {
        super(message);
    }
    public FileException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
    public FileException(String message, Throwable cause) {
        super(message, cause);
    }
    public FileException(Throwable cause) {
        super(cause);
    }
    protected FileException(String message, Throwable cause,
                            boolean enableSuppression,
                            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
