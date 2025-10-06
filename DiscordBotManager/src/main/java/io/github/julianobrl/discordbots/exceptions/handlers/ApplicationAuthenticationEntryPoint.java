package io.github.julianobrl.discordbots.exceptions.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.julianobrl.discordbots.entities.dtos.ErrorMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ApplicationAuthenticationEntryPoint  implements AuthenticationEntryPoint {

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException)
            throws IOException {

        log.error("Authentication exception occurred for request: {}", request, authException);

        ErrorMessage apiErrorResponse = new ErrorMessage(HttpStatus.UNAUTHORIZED,authException.getMessage());

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(response.getOutputStream(), apiErrorResponse);
    }
}