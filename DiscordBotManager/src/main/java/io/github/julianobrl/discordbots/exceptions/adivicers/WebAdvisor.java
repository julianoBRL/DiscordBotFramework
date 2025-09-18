package io.github.julianobrl.discordbots.exceptions.adivicers;

import io.github.julianobrl.discordbots.entities.ErrorMessage;
import io.github.julianobrl.discordbots.exceptions.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice(annotations = Controller.class)
public class WebAdvisor {

    @ExceptionHandler(BotException.class)
    public ResponseEntity<ErrorMessage> handleBotException(BotException ex) {
        return ResponseEntity
                .status(ex.getStatus())
                .body(ErrorMessage.builder()
                        .message(ex.getMessage())
                        .status(ex.getStatus())
                        .build());
    }

    @ExceptionHandler(DockerException.class)
    public ResponseEntity<ErrorMessage> handleDockerException(DockerException ex) {
        return ResponseEntity
                .status(ex.getStatus())
                .body(ErrorMessage.builder()
                        .message(ex.getMessage())
                        .status(ex.getStatus())
                        .build());
    }

    @ExceptionHandler(FileException.class)
    public ResponseEntity<ErrorMessage> handleFileException(FileException ex) {
        return ResponseEntity
                .status(ex.getStatus())
                .body(ErrorMessage.builder()
                        .message(ex.getMessage())
                        .status(ex.getStatus())
                        .build());
    }

    @ExceptionHandler(JsonException.class)
    public ResponseEntity<ErrorMessage> handleJsonException(JsonException ex) {
        return ResponseEntity
                .status(ex.getStatus())
                .body(ErrorMessage.builder()
                        .message(ex.getMessage())
                        .status(ex.getStatus())
                        .build());
    }

    @ExceptionHandler(PluginException.class)
    public ResponseEntity<ErrorMessage> handlePluginException(PluginException ex) {
        return ResponseEntity
                .status(ex.getStatus())
                .body(ErrorMessage.builder()
                        .message(ex.getMessage())
                        .status(ex.getStatus())
                        .build());
    }

    @ExceptionHandler(RepoException.class)
    public ResponseEntity<ErrorMessage> handleRepoException(RepoException ex) {
        return ResponseEntity
                .status(ex.getStatus())
                .body(ErrorMessage.builder()
                        .message(ex.getMessage())
                        .status(ex.getStatus())
                        .build());
    }

    @ExceptionHandler(VersionException.class)
    public ResponseEntity<ErrorMessage> handleVersionException(VersionException ex) {
        return ResponseEntity
                .status(ex.getStatus())
                .body(ErrorMessage.builder()
                        .message(ex.getMessage())
                        .status(ex.getStatus())
                        .build());
    }

}
