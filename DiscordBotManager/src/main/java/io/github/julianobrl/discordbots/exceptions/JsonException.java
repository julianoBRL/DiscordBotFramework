package io.github.julianobrl.discordbots.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public class JsonException extends RuntimeException {

    @Getter
    private HttpStatus status;

    public JsonException() {
        super();
    }
    public JsonException(String message) {
        super(message);
    }
    public JsonException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
    public JsonException(String message, Throwable cause) {
        super(message, cause);
    }
    public JsonException(Throwable cause) {
        super(cause);
    }
    protected JsonException(String message, Throwable cause,
                            boolean enableSuppression,
                            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
