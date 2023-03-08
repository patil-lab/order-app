package com.prive.ordering.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@ConditionalOnProperty(
        value = "swagger.local",
        havingValue = "true",
        matchIfMissing = false)
@EnableSwagger2
public class SwaggerConfig {

    @Value("${spring.application.name}")
    private String serviceName;

    @Value("${server.port}")
    private Integer port;

    @Value("${api-version}")
    private String apiVersion;

    @Bean
    public Docket createDocket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .forCodeGeneration(true)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.prive.ordering"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(serviceName)
                .description(serviceName + " Doc")
                .termsOfServiceUrl("http://localhost" + port)
                .version(apiVersion)
                .build();
    }


}
