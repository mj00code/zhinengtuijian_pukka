package com.ubo.iptv.swagger;

import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Applications;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author huangjian
 */
@Component
@Primary
@EnableSwagger2
public class SwaggerResourcesConfiguration implements SwaggerResourcesProvider {

    private EurekaClient client;

    public SwaggerResourcesConfiguration(EurekaClient client) {
        this.client = client;
    }

    private static String swaggerPathTemplate = "/api/%s/v2/api-docs?group=%s";

    @Override
    public List<SwaggerResource> get() {
        List resources = new ArrayList<>();
        Applications applications = client.getApplications();
        applications.getRegisteredApplications().forEach(app -> {
            if (app.getInstances().size() > 0) {
                String swagger = Optional.ofNullable(app.getInstances().get(0).getMetadata().get("swagger")).orElse("false");
                if (swagger.equalsIgnoreCase("true") && !CollectionUtils.isEmpty(app.getInstances())) {
                    String vip = app.getInstances().get(0).getVIPAddress();
                    resources.add(swaggerResource(app.getName(), String.format(swaggerPathTemplate, vip, vip), "2.0"));
                }
            }
        });
        return resources;
    }

    private SwaggerResource swaggerResource(String name, String location, String version) {
        SwaggerResource swaggerResource = new SwaggerResource();
        swaggerResource.setName(name);
        swaggerResource.setLocation(location);
        swaggerResource.setSwaggerVersion(version);
        return swaggerResource;
    }
}