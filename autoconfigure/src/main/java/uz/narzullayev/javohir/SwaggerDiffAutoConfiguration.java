package uz.narzullayev.javohir;


import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.SpringDocConfigProperties;
import org.springdoc.webmvc.ui.SwaggerConfig;
import org.springdoc.webmvc.ui.SwaggerConfigResource;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnJava;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.system.JavaVersion;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestTemplate;
import uz.narzullayev.javohir.telegram.model.SendMessage;
import uz.narzullayev.javohir.telegram.model.TgAction;

import java.util.Map;


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
            var html = swaggerDiffService.start();
            telegramRestTemplate.ifAvailable(template -> {
                var path = swaggerDiffProperties.getTelegram().getPath();
                var webHook = swaggerDiffProperties.getTelegram().getWebHook();
                var chatId = swaggerDiffProperties.getTelegram().getChatId();
                log.info("Telegram change swagger diff starting ...-> {}",path);
                template.getForEntity(path+TgAction.SET_WEBHOOK,Void.class);
                template.getForEntity(path+TgAction.SET_WEBHOOK,Void.class,Map.of("url",webHook));
                log.info("Telegram send diff swagger");
                var sendMessage = new SendMessage();
                sendMessage.setText(html);
                sendMessage.setParseMode("Markdown");
                sendMessage.setAllowSendingWithoutReply(true);
                sendMessage.setChatId(chatId);
                template.postForEntity(path + TgAction.SEND_MESSAGE, sendMessage, String.class);
                // template.ge
            });
        } catch (Exception e){
            log.error(e.getMessage());
        }

    }
}
