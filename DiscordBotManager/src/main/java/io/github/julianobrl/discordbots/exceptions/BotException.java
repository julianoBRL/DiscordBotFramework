package io.github.julianobrl.discordbots.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public class BotException extends RuntimeException {

    @Getter
    private HttpStatus status;

    public BotException() {
        super();
    }
    public BotException(String message) {
        super(message);
    }
    public BotException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
    public BotException(String message, Throwable cause) {
        super(message, cause);
    }
    public BotException(Throwable cause) {
        super(cause);
    }
    protected BotException(String message, Throwable cause,
                           boolean enableSuppression,
                           boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
