package io.github.julianobrl.discordbots.configs;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.proc.SecurityContext;
import io.github.julianobrl.discordbots.utils.RsaKeyParser;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;

@Slf4j
@Getter
@Configuration
public class JwtConfigs {

    @Value("${jwt.rsa.publicKey:}")
    private String sPublicKey;

    @Value("${jwt.rsa.privateKey:}")
    private String sPrivateKey;

    private PrivateKey privateKey;
    private PublicKey publicKey;

    public JwtConfigs() throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        if (sPublicKey == null || sPublicKey.isBlank() || sPrivateKey == null || sPrivateKey.isBlank()) {
            log.warn("\n\n" +
                    "######################################################################\n" +
                    "PublicKey and/or privateKey not set, generating demo keys!\n" +
                    "For production it is recommended to set the keys in the application.yml\n" +
                    "Keys will change with each restart/reboot, be warned!!!\n" +
                    "######################################################################\n");

            KeyPair keyPair = generateRsaKeyPair();
            publicKey = keyPair.getPublic();
            privateKey = keyPair.getPrivate();
        }else{
            RsaKeyParser parser = new RsaKeyParser();
            if (sPublicKey.startsWith("file:") && sPrivateKey.startsWith("file:")) {
                String sPublicKeyFilePath = sPublicKey.substring("file:".length());
                String sPrivateKeyFilePath = sPrivateKey.substring("file:".length());

                privateKey = parser.parsePrivateKey(Files.readString(Path.of(sPrivateKeyFilePath)));
                publicKey = parser.parsePublicKey(Files.readString(Path.of(sPublicKeyFilePath)));
            }else {
                privateKey = parser.parsePrivateKey(sPrivateKey);
                publicKey = parser.parsePublicKey(sPublicKey);
            }
        }
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withPublicKey((RSAPublicKey) publicKey).build();
    }

    @Bean
    public JwtEncoder jwtEncoder() {
        // Cria uma JWKSource (JSON Web Key Source) com a chave privada
        JWK jwk = new RSAKey.Builder((RSAPublicKey) publicKey).privateKey(privateKey).build();
        ImmutableJWKSet<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwks);
    }

    private KeyPair generateRsaKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        return keyPairGenerator.generateKeyPair();
    }

}
