package uz.narzullayev.javohir;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.TrustStrategy;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import uz.narzullayev.javohir.telegram.exceptions.TelegramException;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import static org.springframework.http.HttpStatus.Series.CLIENT_ERROR;
import static org.springframework.http.HttpStatus.Series.SERVER_ERROR;
import static uz.narzullayev.javohir.telegram.model.BotMessageEnum.EXCEPTION_API;

@Slf4j
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(
        prefix = "springdoc.swagger-ui.diff",
        name = "telegram.api-key"
)
public class TelegramConfig {

    @Bean
    @ConditionalOnMissingBean

    public RestTemplate telegramTemplate()
            throws NoSuchAlgorithmException,
            KeyStoreException,
            KeyManagementException
    {
        TrustStrategy acceptingTrustStrategy = (x509Certificates, s) -> true;

        var sslContext = org.apache.http.ssl.SSLContexts.custom()
                .loadTrustMaterial(null, acceptingTrustStrategy)
                .build();

        var csf = new SSLConnectionSocketFactory(sslContext);
        var httpClient = HttpClients.custom()
                .setSSLSocketFactory(csf)
                .build();
        var requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient(httpClient);

        var restTemplate = new RestTemplate(requestFactory);
        restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory("https://api.telegram.org"));
        restTemplate.setErrorHandler(getResponseErrorHandler());
        return restTemplate;
    }

    @SuppressWarnings("NullableProblems")
    private static ResponseErrorHandler getResponseErrorHandler() {
        return new ResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse httpResponse) throws IOException {
                return (
                        httpResponse.getStatusCode().series() == CLIENT_ERROR
                                || httpResponse.getStatusCode().series() == SERVER_ERROR);
            }

            @Override
            public void handleError(ClientHttpResponse httpResponse) throws IOException {
                switch (httpResponse.getStatusCode().series()) {
                    case SERVER_ERROR:
                        // handle SERVER_ERROR
                        break;
                    case CLIENT_ERROR:
                        // handle CLIENT_ERROR
                        if (httpResponse.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                            throw new TelegramException.InternalErrorException(EXCEPTION_API.getMessage());
                        }
                        break;
                }
            }
        };
    }
/*
    public ResultTelegram<?> sendMessageToUser(
            SendMessage sendMessage,
            SwaggerDiffProperties swaggerDiffProperties
    ) {
        try {
            var path = swaggerDiffProperties.getTelegram()
                    .getPath();
           /// return telegramFeign.sendMessageToUser(path, sendMessage);
            return null;
        } catch (FeignException e) {
            throw new TelegramException.DefaultException(sendMessage.getChatId(), e.getMessage());
        }
    }*/

    @EventListener(classes = { ContextRefreshedEvent.class})
    public void handleMultipleEvents() {
        log.info("Telegram swagger difference endpoint ");

    }


}
