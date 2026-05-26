package br.com.fiap.techchallenge.hospital.scheduling.api;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Hospital Appointment Platform - Scheduling API",
                version = "v1",
                description = "API REST para agendamento, remarcacao e atualizacao de historico clinico de consultas. Autenticacao via JWT Bearer obtido em POST /api/v1/auth/login.",
                contact = @Contact(name = "FIAP Tech Challenge"),
                license = @License(name = "Academic")
        )
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class OpenApiConfig {
}
