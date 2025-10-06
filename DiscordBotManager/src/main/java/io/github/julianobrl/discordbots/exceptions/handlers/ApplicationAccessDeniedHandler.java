package io.github.julianobrl.discordbots.exceptions.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.julianobrl.discordbots.entities.dtos.ErrorMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ApplicationAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException)
            throws IOException {

        log.error("Access denied for request: {}", request, accessDeniedException);

        ErrorMessage apiErrorResponse = new ErrorMessage(HttpStatus.UNAUTHORIZED,accessDeniedException.getMessage());

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(response.getOutputStream(), apiErrorResponse);
    }
}
