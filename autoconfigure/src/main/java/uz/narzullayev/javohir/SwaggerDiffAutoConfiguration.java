package uz.narzullayev.javohir;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.SpringDocConfigProperties;
import org.springdoc.webmvc.ui.SwaggerConfig;
import org.springdoc.webmvc.ui.SwaggerConfigResource;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.system.JavaVersion;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestTemplate;


@Slf4j
@Configuration(proxyBeanMethods = false)
@Import(TelegramConfig.class)
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
    private final  SpringDocConfigProperties springDocConfigProperties;

    @Qualifier("telegramTemplate")
    private final ObjectProvider<RestTemplate> telegramRestTemplate;

    public SwaggerDiffAutoConfiguration(Environment environment,
                                        SwaggerDiffProperties swaggerDiffProperties,
                                        SpringDocConfigProperties springDocConfigProperties,
                                        ObjectProvider<RestTemplate> telegramRestTemplate) {
        this.environment = environment;
        this.swaggerDiffProperties = swaggerDiffProperties;
        this.springDocConfigProperties = springDocConfigProperties;
        this.telegramRestTemplate = telegramRestTemplate;
    }


    @EventListener(classes = { ContextRefreshedEvent.class})
    public void handleMultipleEvents() {
        log.info("Checking swagger difference endpoint : {}",swaggerDiffProperties.getPathWeb());
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
        telegramRestTemplate.ifAvailable(template -> {
            log.info("Telegram change swagger diff starting ...");
        });
    }
}
