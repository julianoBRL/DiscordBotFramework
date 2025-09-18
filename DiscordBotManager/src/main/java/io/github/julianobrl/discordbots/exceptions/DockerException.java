package io.github.julianobrl.discordbots.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public class DockerException extends RuntimeException {

    @Getter
    private HttpStatus status;

    public DockerException() {
        super();
    }
    public DockerException(String message) {
        super(message);
    }
    public DockerException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
    public DockerException(String message, Throwable cause) {
        super(message, cause);
    }
    public DockerException(Throwable cause) {
        super(cause);
    }
    protected DockerException(String message, Throwable cause,
                              boolean enableSuppression,
                              boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
