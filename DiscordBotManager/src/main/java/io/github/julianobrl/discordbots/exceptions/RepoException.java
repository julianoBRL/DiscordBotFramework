package io.github.julianobrl.discordbots.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public class RepoException extends RuntimeException {

    @Getter
    private HttpStatus status;

    public RepoException() {
        super();
    }
    public RepoException(String message) {
        super(message);
    }
    public RepoException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
    public RepoException(String message, Throwable cause) {
        super(message, cause);
    }
    public RepoException(Throwable cause) {
        super(cause);
    }
    protected RepoException(String message, Throwable cause,
                            boolean enableSuppression,
                            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
