package uz.narzullayev.javohir;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Arrays;


@SpringBootApplication
public class SwaggerDiffApplication {

    public static void main(String[] args) {
        var run = SpringApplication.run(SwaggerDiffApplication.class, args);
        Arrays.stream(run.getBeanDefinitionNames()).forEach(System.out::println);
    }
}

