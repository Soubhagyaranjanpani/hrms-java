package com.hrms.config;



import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
public class SwaggerConfig {

    private SecurityScheme createAPIKeyScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .bearerFormat("JWT")
                .scheme("Bearer")
                .description("""
                        JWT Authentication
                        -----------------
                        To authenticate, use the `/employee/login` endpoint to get a token.
                        Then include the token in the Authorization header:
                        
                        Authorization: Bearer <your-token>
                        """);
    }





        @Bean
        public OpenAPI customOpenAPI() {
            return new OpenAPI()
                    .info(new Info()
                            .title("HRMS API")
                            .version("1.0")
                            .description("Human Resource Management System API"))
                    .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                    .components(new Components()
                            .addSecuritySchemes("bearerAuth",
                                    new SecurityScheme()
                                            .type(SecurityScheme.Type.HTTP)
                                            .scheme("bearer")
                                            .bearerFormat("JWT")));
        }


    private List<Server> getServers() {
        return Arrays.asList(
                new Server()
                        .url("http://localhost:8080/hrms")
                        .description("Development Server"),
                new Server()
                        .url("https://staging.hrms.com/hrms")
                        .description("Staging Server"),
                new Server()
                        .url("https://api.hrms.com/hrms")
                        .description("Production Server")
        );
    }

    private List<Tag> getTags() {
        return Arrays.asList(
                new Tag().name("Authentication Controller")
                        .description("APIs for authentication - Login, Logout, Password Management"),
                new Tag().name("Employee Controller")
                        .description("APIs for employee management - Create, Update, Delete, View employees"),
                new Tag().name("Role Controller")
                        .description("APIs for role management - Manage user roles and permissions"),
                new Tag().name("Department Controller")
                        .description("APIs for department management"),
                new Tag().name("Branch Controller")
                        .description("APIs for branch/location management")
        );
    }
}
