package com.payment.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.HeaderParameter;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.servers.Server;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springdoc.core.customizers.OpenApiCustomizer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@RequiredArgsConstructor
public class OpenApiConfiguration {
    @Value("${api.version:}")
    private String version;

    @Value("${api.title:}")
    private String title;

    @Value("${api.description:}")
    private String description;

    @Value("${api.url:}")
    private String url;

    private final Optional<BuildProperties> buildInfo;

    @Bean
    @ConditionalOnProperty(prefix = "api", name = {"title"})
    public OpenAPI openAPI() {
        String apiVersion = StringUtils.defaultString(version, buildInfo.isEmpty() ? "v1.0.0" : buildInfo.get().getVersion());
        Info info = new Info()
                .version(apiVersion)
                .title(title)
                .description(description);
        Server server = new Server();
        server.setUrl(url);
        return new OpenAPI()
                .info(info)
                .servers(List.of(server));
    }

    @Bean
    public OpenApiCustomizer openAPICustomizer() {
        return openApi -> {
            openApi.getPaths() // Swagger 문서의 API 경로(Path)를 가져옴
                    .values() // API 경로(Path)에 대한 값들을 추출
                    .stream() // 값들을 스트림으로 변환하여 각 API 경로(Path)에 대한 작업(Operation)을 개별적으로 처리
                    .flatMap(pathItem -> pathItem.readOperations().stream()) // 각 API 경로(Path)에 대한 작업(Operation)을 하나의 스트림으로 평탄화
                    .forEach(this::resetOperation);
        };
    }

    private void resetOperation(Operation operation) {
        addXAuthToken(operation);
    }

    //  Swagger 문서에 X-AUTH-TOKEN 헤더를 자동으로 추가
    private void addXAuthToken(Operation operation) {
        // 연산(Operation) 객체의 파라미터 목록이 비어있는 경우, 새로운 빈 파라미터 목록을 생성하여 할당
        if (operation.getParameters() == null) {
            operation.setParameters(new ArrayList<>());
        }

        List<Integer> removeList = new ArrayList<>();
        for (int i = 0; i < operation.getParameters().size(); i++) {
            Parameter param = operation.getParameters().get(i);
            if (param != null) {
                Schema schema = param.getSchema();
                if (schema != null) {
                    if (StringUtils.equalsIgnoreCase(schema.get$ref(), "#/components/schemas/AuthUser") || StringUtils.equalsIgnoreCase(schema.get$ref(), "#/components/schemas/AuthUserWithName") ) {
                        removeList.add(i);
                    }
                }
            }
        }

        // 제거 목록에 추가된 인덱스들을 사용하여 연산(Operation)의 파라미터 목록에서 특정 파라미터를 제거
        for (int index : removeList) {
            operation.getParameters().remove(index);
        }

        // X-AUTH-TOKEN 헤더 파라미터를 생성하고 연산(Operation)의 파라미터 목록의 맨 앞에 추가
        HeaderParameter parameter = new HeaderParameter();
        parameter.name("X-AUTH-TOKEN");
        operation.getParameters().add(0, parameter);
    }
}
