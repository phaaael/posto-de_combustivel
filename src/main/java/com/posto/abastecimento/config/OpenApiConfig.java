package com.posto.abastecimento.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI openAPI() {
        return new OpenAPI().info(new Info()
                .title("Sistema de Abastecimentos")
                .version("v1")
                .description("API para gerenciamento de combustiveis, bombas e abastecimentos."));
    }
}
