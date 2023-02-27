package uz.narzullayev.javohir;


import org.springdoc.webmvc.ui.SwaggerConfig;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration(proxyBeanMethods = false)
@Import(AutoConfigureSwaggerDifImportBeanDefinitionRegistrar.class)
@ConditionalOnProperty(name = "springdoc.swagger-ui.enabled", havingValue = "true", matchIfMissing = true)
@AutoConfigureBefore(SwaggerConfig.class)
@EnableConfigurationProperties({SwaggerDiffProperties.class})
public class SwaggerDiffAutoConfiguration {

}
