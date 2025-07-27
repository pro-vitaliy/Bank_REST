package com.example.bankcards.config;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.RSAKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Configuration
public class RsaKeysConfig {

    @Value("${rsa.private-key}")
    private Resource privateKeyResource;

    @Value("${rsa.public-key}")
    private Resource publicKeyResource;

    @Bean
    public RSAKey rsaKey() {
        try {
            String publicPem = publicKeyResource.getContentAsString(StandardCharsets.UTF_8);
            String privatePem = privateKeyResource.getContentAsString(StandardCharsets.UTF_8);
            String fullPem = String.join("\n", publicPem, privatePem);

            return RSAKey.parseFromPEMEncodedObjects(fullPem).toRSAKey();
        } catch (IOException | JOSEException e) {
            throw new IllegalStateException("Ошибка при загрузке RSA ключей", e);
        }
    }
}
