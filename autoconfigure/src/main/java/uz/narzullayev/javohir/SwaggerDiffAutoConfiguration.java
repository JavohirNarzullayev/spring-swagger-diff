package uz.narzullayev.javohir;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.SpringDocConfigProperties;
import org.springdoc.webmvc.ui.SwaggerConfig;
import org.springdoc.webmvc.ui.SwaggerConfigResource;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.system.JavaVersion;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;


@Slf4j
@Configuration(proxyBeanMethods = false)
@RequiredArgsConstructor
@ConditionalOnWebApplication
@AutoConfigureAfter({SwaggerConfig.class})
@ConditionalOnClass({SwaggerConfigResource.class})
@ConditionalOnProperty(
        name = {"springdoc.swagger-ui.enabled", "springdoc.swagger-ui.diff.enabled"},
        havingValue = "true",
        matchIfMissing = true
)
@EnableConfigurationProperties({SwaggerDiffProperties.class})
@ConditionalOnJava(value = JavaVersion.SEVENTEEN,range = ConditionalOnJava.Range.EQUAL_OR_NEWER)
public class SwaggerDiffAutoConfiguration {

    private final Environment environment;
    private final SwaggerDiffProperties swaggerDiffProperties;
    private final SpringDocConfigProperties springDocConfigProperties;

    @EventListener(classes = { ContextRefreshedEvent.class})
    public void handleMultipleEvents() {
        log.info("Checking swagger difference");
        var swaggerDiffService = new SwaggerDiffService(
                environment,
                swaggerDiffProperties,
                springDocConfigProperties
        );
        try {
            swaggerDiffService.start();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
    @Bean
    @Lazy(false)
    @ConditionalOnMissingBean
    @ConditionalOnProperty(
            name = "springdoc.swagger-ui.diff.enabled",
            havingValue = "true",
            matchIfMissing = true
    )
    SwaggerDiffController swaggerDiffController() {
        log.info("Swagger ui path difference : {}", swaggerDiffProperties.getPathWeb());
        return new SwaggerDiffController(swaggerDiffProperties);
    }
}
