package io.github.julianobrl.discordbots.services;

import io.github.julianobrl.discordbots.entities.dtos.AuthResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtEncoder encoder;
    private final JwtDecoder decoder;

    public AuthResponse create(Long userId, String username, Collection<? extends GrantedAuthority> authorities) {
        Instant now = Instant.now();
        // Converte as permissões/roles do Spring Security para o formato do JWT
        String scope = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));

        Instant expiration = now.plus(4, ChronoUnit.HOURS);

        // Cria as claims (o payload)
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self") // Emissor do token
                .issuedAt(now)
                .expiresAt(expiration) // Expira em 1 hora
                .subject(username) // O "username" como Subject
                .claim("id", userId)
                .claim("scope", scope) // Adiciona as roles
                .build();

        // Assina (criptografa) o token com a chave privada
        String token = encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
        return new AuthResponse(token,expiration);
    }

    public Jwt validate(String token) throws JwtValidationException {
        return decoder.decode(token);
    }

    public Long getPayloadId(String token) throws JwtValidationException {
        Jwt jwt = validate(token);
        return jwt.getClaim("id");
    }

    public String extractUsername(String token) {
        Jwt jwt = validate(token);
        return jwt.getSubject();
    }

}
