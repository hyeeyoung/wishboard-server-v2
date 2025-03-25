package com.wishboard.server.config.swagger;

import java.util.Map;

import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

import org.springdoc.core.customizers.OperationCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;

@Configuration
public class SwaggerConfig {

    @Bean
    public OperationCustomizer operationCustomizer() {
        return (Operation operation, HandlerMethod handlerMethod) -> {
            Parameter userAgentHeader = new Parameter()
                .in(ParameterIn.HEADER.toString())  // 전역 헤더 설정
                .schema(new StringSchema()._default("wishboard-server/local").name("User-Agent")) // default값 설정
                .name("User-Agent")
                .description("사용자 에이전트. ex. wishboard-ios/dev, wishboard-aos/prod")
                .required(true);
            operation.addParametersItem(userAgentHeader);
            return operation;
        };
    }

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
            .group("v2")
            .packagesToScan("com.wishboard.server.controller")
            .pathsToMatch("/v2/**")
            .addOperationCustomizer(operationCustomizer())
            .build();
    }

    @Bean
    public OpenAPI customOpenAPI() {
        Info info = new Info()
            .title("Wishboard API Docs")
            .version("2.0.0")
            .description("API 명세서");

        SecurityScheme securityScheme = new SecurityScheme()
            .type(SecurityScheme.Type.HTTP)
            .scheme("bearer")
            .bearerFormat("JWT")
            .in(SecurityScheme.In.HEADER)
            .name("Authorization");

        SecurityRequirement securityRequirement = new SecurityRequirement().addList("Authentication");

        return new OpenAPI()
            .components(
                new Components()
                    .addSecuritySchemes("Authentication", securityScheme)
            )
            .info(info)
            .addSecurityItem(securityRequirement);
    }
}
