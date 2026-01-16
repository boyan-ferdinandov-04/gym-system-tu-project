package com.example.gym_management.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI gymManagementOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Gym Management API")
                        .description("REST API for managing gym operations including members, trainers, " +
                                "classes, bookings, memberships, and payments. This API provides comprehensive " +
                                "endpoints for gym administration and member management.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Gym Management Team")
                                .email("support@gymmanagement.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Development Server")))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT Authorization header using the Bearer scheme. " +
                                        "Enter your token in the text input below.")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}
