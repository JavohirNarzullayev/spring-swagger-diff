package uz.narzullayev.javohir;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.SpringDocConfigProperties;
import org.springdoc.webmvc.ui.SwaggerConfig;
import org.springdoc.webmvc.ui.SwaggerConfigResource;
import org.springdoc.webmvc.ui.SwaggerWelcomeCommon;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import static org.springdoc.core.Constants.SPRINGDOC_USE_MANAGEMENT_PORT;

@Slf4j
@EnableScheduling
@Configuration(proxyBeanMethods = false)
@RequiredArgsConstructor
@ConditionalOnWebApplication
@AutoConfigureAfter({SwaggerConfig.class})
@ConditionalOnClass({SwaggerConfigResource.class, Scheduled.class})
@ConditionalOnProperty(
 name = {"springdoc.swagger-ui.enabled", "springdoc.swagger-ui.diff.enabled"},
 havingValue = "true",
 matchIfMissing = true
)
@EnableConfigurationProperties({SwaggerDiffProperties.class})
public class SwaggerDiffAutoConfiguration {

    private final Environment environment;
    private final SwaggerDiffProperties swaggerDiffProperties;
    private final SpringDocConfigProperties springDocConfigProperties;

    @Scheduled(initialDelay = 3000, fixedDelay = Long.MAX_VALUE)
    public void swaggerDiffService() {
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
    @ConditionalOnMissingBean
    @ConditionalOnProperty(name = "springdoc.swagger-ui.diff.enabled", havingValue = "false", matchIfMissing = true)
    @Lazy(false)
    SwaggerDiffController swaggerDiffController() {
        return new SwaggerDiffController(swaggerDiffProperties);
    }
}
