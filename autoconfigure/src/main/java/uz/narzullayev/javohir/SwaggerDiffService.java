package uz.narzullayev.javohir;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springdoc.core.SpringDocConfigProperties;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestTemplate;
import uz.narzullayev.javohir.model.ChangedOpenApi;
import uz.narzullayev.javohir.output.HtmlRender;
import uz.narzullayev.javohir.output.MarkdownRender;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

@AllArgsConstructor
@NoArgsConstructor
public class SwaggerDiffService {
    private Environment environment;
    private SwaggerDiffProperties swaggerDiffProperties;
    private SpringDocConfigProperties springDocConfigProperties;

    public void start() {
        System.out.println(swaggerDiffProperties.getLatestDocPath());
        String path = springDocConfigProperties.getApiDocs().getPath();
        System.out.println(path);
        var latestDocPath = swaggerDiffProperties.getLatestDocPath();
        var directory = new File(latestDocPath);
        if (!directory.exists()) {
            directory.mkdir();
        }

        var filename = "latest_swagger.json";
        var filePath = Paths.get(directory.getAbsolutePath(), filename);
        boolean existFile = Files.exists(filePath);
        String latest = "null";
        String newVersion = "null";
        if (existFile) {
            try {
                latest = new String(Files.readAllBytes(filePath));
                newVersion = getDocStr();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }

        } else {
            latest = getDocStr();
            newVersion = latest;
            var in = new ByteArrayInputStream(latest.getBytes());
            try {
                Files.copy(in, filePath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
        final var diff = OpenApiCompare.fromContents(latest, newVersion);
        var html = new HtmlRender("Changelog",
                "http://deepoove.com/swagger-diff/stylesheets/demo.css")
                .render(diff);
        try {
            var diffHtml = Paths.get(directory.getAbsolutePath(),  "diff_swagger.html");
            var fw = new FileWriter(diffHtml.getFileName().toFile());
            fw.write(html);
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getDocStr() {
        var serverPort = Optional.ofNullable(
                        environment.getProperty("server.port"))
                .orElse("8080");
        var path = springDocConfigProperties.getApiDocs().getPath();
        var url = "http://localhost:" + serverPort + path;
        var forEntity = new RestTemplate().getForEntity(
                url,
                String.class
        );
        return forEntity.getBody();
    }
}
