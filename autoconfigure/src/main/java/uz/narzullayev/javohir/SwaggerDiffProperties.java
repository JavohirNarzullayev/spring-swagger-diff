package uz.narzullayev.javohir;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "springdoc.swagger-ui.diff")
@Getter @Setter
@ToString
public class SwaggerDiffProperties {
    private boolean enabled = true;
    private String pathWeb = "/changelog";
    private String latestDocPath = "upload_folder";
    private Telegram telegram;

    @Getter
    @Setter
    @ToString
    public static class Telegram {
        private String apiKey;
        private String webHook;
        private String chatId;

        public String getPath() {
            return "bot" + this.apiKey;
        }

    }
}
