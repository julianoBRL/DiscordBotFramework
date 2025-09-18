package io.github.julianobrl.discordbots.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public class PluginException extends RuntimeException {

    @Getter
    private HttpStatus status;

    public PluginException() {
        super();
    }
    public PluginException(String message) {
        super(message);
    }
    public PluginException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
    public PluginException(String message, Throwable cause) {
        super(message, cause);
    }
    public PluginException(Throwable cause) {
        super(cause);
    }
    protected PluginException(String message, Throwable cause,
                           boolean enableSuppression,
                           boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
