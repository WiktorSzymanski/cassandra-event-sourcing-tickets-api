package com.szymanski.wiktor.ticketsApi.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {

    @Bean
    fun openApi(): OpenAPI = OpenAPI()
        .info(
            Info()
                .title("Tickets API")
                .description("API documentation for Tickets application that uses Cassandra and Event Sourcing")
                .version("v0.1")
        )
}
