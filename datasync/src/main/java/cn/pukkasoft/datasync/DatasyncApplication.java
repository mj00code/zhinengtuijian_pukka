package cn.pukkasoft.datasync;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Date;

@SpringBootApplication
@PropertySource(ignoreResourceNotFound = true, value = "classpath:base.properties")
@PropertySource(ignoreResourceNotFound = true, value = "classpath:base-${spring.profiles.active}.properties")
@EnableTransactionManagement
@EnableSwagger2
@MapperScan("cn.pukkasoft.datasync.dao")
@EnableAsync
@EnableEurekaClient
//@EnableScheduling
public class DatasyncApplication {

    public static void main(String[] args) {
        SpringApplication.run(DatasyncApplication.class, args);
    }

    private CorsConfiguration buildConfig() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.addAllowedOrigin("*");
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("*");
        return corsConfiguration;
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", buildConfig());
        return new CorsFilter(source);
    }

    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(5000);// 设置超时
        requestFactory.setReadTimeout(5000);
        RestTemplate restTemplate = new RestTemplate(requestFactory);
        return restTemplate;
    }

    @Bean
    public Docket swaggerApi(@Value("${spring.application.name}") String groupName,
                             @Value("${swagger.host}") String host) {
        return new Docket(DocumentationType.SWAGGER_2).host(host).groupName(groupName).select().apis(RequestHandlerSelectors.withClassAnnotation(RestController.class)).build().directModelSubstitute(Date.class, String.class).apiInfo(new ApiInfoBuilder().title(groupName).description(groupName).build());
    }
}
