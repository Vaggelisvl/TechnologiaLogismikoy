package com.technology.technologysoftware.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.stereotype.Component;

@Component
@OpenAPIDefinition(info = @Info(title = "API", version = "v1", description = "Some Description"))
public class SwaggerConfig {
}
