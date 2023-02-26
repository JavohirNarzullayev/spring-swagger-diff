package uz.narzullayev.javohir;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;


@SpringBootApplication
public class SwaggerDiffApplication {
    private final static String OPENAPI_DOC1="http://localhost:8080/v1/test/api-docs";
    private final static String OPENAPI_DOC2="https://mobile-id-api.licenses.uz/v1/swagger/api-docs/application";

    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(SwaggerDiffApplication.class, args);
    }
}

