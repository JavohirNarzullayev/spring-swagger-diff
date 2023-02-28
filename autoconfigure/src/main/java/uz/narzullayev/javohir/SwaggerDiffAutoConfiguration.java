package uz.narzullayev.javohir;


import lombok.RequiredArgsConstructor;
import org.springdoc.core.SpringDocConfigProperties;
import org.springdoc.core.SwaggerUiConfigProperties;
import org.springdoc.ui.AbstractSwaggerWelcome;
import org.springdoc.webmvc.ui.SwaggerConfig;
import org.springdoc.webmvc.ui.SwaggerConfigResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration;
import org.springframework.boot.autoconfigure.web.embedded.EmbeddedWebServerFactoryCustomizerAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@EnableScheduling
@Configuration(proxyBeanMethods = false)
@RequiredArgsConstructor
@ConditionalOnWebApplication
@AutoConfigureAfter({SwaggerConfig.class})
@ConditionalOnClass({SwaggerConfigResource.class, Scheduled.class})
@ConditionalOnProperty(name = "springdoc.swagger-ui.enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties({SwaggerDiffProperties.class})
public class SwaggerDiffAutoConfiguration {

    private final Environment environment;
    private final SwaggerDiffProperties swaggerDiffProperties;
    private final SpringDocConfigProperties springDocConfigProperties;

    @Scheduled(initialDelay = 3000, fixedDelay = Long.MAX_VALUE)
    public void swaggerDiffService(

    ) {
        var swaggerDiffService = new SwaggerDiffService(
                environment,
                swaggerDiffProperties,
                springDocConfigProperties
        );
        swaggerDiffService.start();
    }
}
