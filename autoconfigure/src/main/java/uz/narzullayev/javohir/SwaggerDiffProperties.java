package uz.narzullayev.javohir;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "springdoc.swagger-ui.diff")
@Getter @Setter
public class SwaggerDiffProperties {
    private boolean enabled = true;
    private String latestDocPath = "upload_folder";

}
