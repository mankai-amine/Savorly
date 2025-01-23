package org.styd.intproj.savorly.config;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("The APIs of Savorly")
                        .description("Endpoints in APIs")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("FSD12.Team3")
                                .url("http://www.styd.org")
                                .email("savorlyFSD12@google.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://springdoc.org")))
                .externalDocs(new ExternalDocumentation()
                        .description("Documentation")
                        .url("https://example.com/docs"));
    }
}
