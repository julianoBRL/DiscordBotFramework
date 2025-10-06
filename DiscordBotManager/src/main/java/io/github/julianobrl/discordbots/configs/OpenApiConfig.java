package io.github.julianobrl.discordbots.configs;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    final String securitySchemeName = "bearerAuth";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Discord Bot Manager")
                        .description("Backend to manage your discord bots")
                        .license(new License()
                                .name("Licença MIT")
                                .url("https://opensource.org/licenses/MIT")))
            .components(new Components()
                .addSecuritySchemes(securitySchemeName,
                        new SecurityScheme()
                                .name("Authorization") // Nome do Header
                                .type(SecurityScheme.Type.HTTP) // Tipo de autenticação: HTTP
                                .scheme("bearer") // Esquema: bearer
                                .bearerFormat("JWT") // Formato (opcional, mas recomendado para JWT)
                                .in(SecurityScheme.In.HEADER) // Onde o token está: no Header
                                .description("Bearer Authorization")
                )
        );
    }
}
