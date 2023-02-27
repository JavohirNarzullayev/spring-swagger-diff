package uz.narzullayev.javohir;

import org.springdoc.webmvc.ui.SwaggerConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.quartz.QuartzAutoConfiguration;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.Profiles;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.Optional;

public class SwaggerDiffAutoConfigurationEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment env, SpringApplication application) {
        if (env.getProperty("springdoc.swagger-ui.enabled", Boolean.class, true)) {
            final var newPropertyValueOpt = Optional.ofNullable(env.getProperty("spring.autoconfigure.exclude"))
                    .map(StringUtils::commaDelimitedListToStringArray)
                    .map(arr -> StringUtils.addStringToArray(arr, SwaggerDiffAutoConfiguration.class.getName()))
                    .map(StringUtils::arrayToCommaDelimitedString);

            newPropertyValueOpt.ifPresent(newPropertyValue -> {
                env.getPropertySources().addFirst(new MapPropertySource("swagger_diff",
                        Map.of("spring.autoconfigure.exclude", newPropertyValue)));
            });
            if (env.acceptsProfiles(Profiles.of("dev"))) {
                System.out.println("pl");
            }



        }
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 100;
    }
}
