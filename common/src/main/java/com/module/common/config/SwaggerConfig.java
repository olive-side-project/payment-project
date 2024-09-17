package com.module.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class SwaggerConfig {

    @Value("${api.version:}")
    private String version;

    @Value("${api.title:}")
    private String title;

    @Value("${api.description:}")
    private String description;

    @Value("${api.url:}")
    private String url;

    private final Optional<BuildProperties> buildInfo;

    public SwaggerConfig(Optional<BuildProperties> buildInfo) {
        this.buildInfo = buildInfo;
    }

    @Bean
    public GroupedOpenApi api() {
        return GroupedOpenApi.builder()
                .group("payment-project-api")
                .pathsToMatch("/**")
                .build();
    }

    @Bean
    public OpenAPI openAPI() {
        String apiVersion = Objects.toString(version, buildInfo.isEmpty() ? "v1.0.0" : buildInfo.get().getVersion());
        Info info = new Info()
                .version(apiVersion)
                .title(title)
                .description(description);
        Server server = new Server();
        server.setUrl(url);

        return new OpenAPI()
                .info(info)
                .servers(List.of(server))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .name("X-AUTH-TOKEN")))
                .security(List.of(new SecurityRequirement().addList("bearerAuth")));
    }
}
