package io.github.julianobrl.discordbots.utils;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

// Esta classe pode ser inicializada (por exemplo, em um método @PostConstruct)
// após a injeção das suas Strings sPublicKey e sPrivateKey.
public class RsaKeyParser {

    // Regex para limpar cabeçalhos e quebras de linha PEM
    private static final String PEM_PRIVATE_HEADER = "-----BEGIN( RSA)? PRIVATE KEY-----";
    private static final String PEM_PRIVATE_FOOTER = "-----END( RSA)? PRIVATE KEY-----";
    private static final String PEM_PUBLIC_HEADER = "-----BEGIN PUBLIC KEY-----";
    private static final String PEM_PUBLIC_FOOTER = "-----END PUBLIC KEY-----";

    /**
     * Converte uma String de chave pública RSA (Base64 X.509 ou PEM) para PublicKey.
     */
    public PublicKey parsePublicKey(String publicKeyString) throws NoSuchAlgorithmException, InvalidKeySpecException {
        // Limpa cabeçalhos/rodapés PEM e quebras de linha
        String keyContent = publicKeyString
                .replaceAll(System.lineSeparator(), "")
                .replaceAll(PEM_PUBLIC_HEADER, "")
                .replaceAll(PEM_PUBLIC_FOOTER, "")
                .trim();

        // Decodifica a string Base64 para bytes
        byte[] keyBytes = Base64.getDecoder().decode(keyContent);

        // Cria a especificação X.509 (padrão para chave pública)
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);

        // Gera o objeto PublicKey
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }

    /**
     * Converte uma String de chave privada RSA (Base64 PKCS#8 ou PEM) para PrivateKey.
     */
    public PrivateKey parsePrivateKey(String privateKeyString) throws NoSuchAlgorithmException, InvalidKeySpecException {
        // Limpa cabeçalhos/rodapés PEM e quebras de linha
        String keyContent = privateKeyString
                .replaceAll(System.lineSeparator(), "")
                .replaceAll(PEM_PRIVATE_HEADER, "")
                .replaceAll(PEM_PRIVATE_FOOTER, "")
                .trim();

        // Decodifica a string Base64 para bytes
        byte[] keyBytes = Base64.getDecoder().decode(keyContent);

        // Cria a especificação PKCS#8 (padrão para chave privada)
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);

        // Gera o objeto PrivateKey
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }
}
