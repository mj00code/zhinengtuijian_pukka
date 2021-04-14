package com.ubo.iptv.swagger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Date;

@Configuration
@ConditionalOnWebApplication
@EnableSwagger2
@ConditionalOnExpression("${spring.resources.add-mappings:true}")
public class SwaggerConfiguration {

    @Value("${spring.application.name}")
    private String groupName;

    @Value("${swagger.host}")
    private String host;

    @Bean
    public Docket swaggerApi() {
        return new Docket(DocumentationType.SWAGGER_2).host(host).groupName(this.groupName).select().apis(RequestHandlerSelectors.withClassAnnotation(RestController.class)).build().directModelSubstitute(Date.class, String.class).apiInfo(new ApiInfoBuilder().title(this.groupName).description(this.groupName).build());
    }
}
