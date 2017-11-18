package com.asura.restapi.swagger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mario on 2017/9/25.
 * Swagger配置类
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Value("${swagger.show}")
    private boolean swaggerShow;

    @Bean
    public Docket createRestApi() {
        List<Parameter> list = new ArrayList<>();
        list.add(new ParameterBuilder().name("Token").description("登录令牌").modelRef(new ModelRef
                ("string")).parameterType("header").required(false).build());
        return new Docket(DocumentationType.SWAGGER_2)
                .enable(this.swaggerShow)
                .apiInfo(new ApiInfoBuilder()
                        .title("大标题")//大标题
                        .description("描述。<br/>" +
                                "#接口约定#<br/>" +
                                "需要登录验证的接口位于/control/路径下<br/>" +
                                "其他接口位于/uncontrol/路径下")
                        .version("1.0.0")//版本
                        .build())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.fetchking.restapi.controller"))
                .paths(PathSelectors.any())
                .build()
                .globalOperationParameters(list);
    }
}
