package uz.narzullayev.javohir;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.springdoc.core.SpringDocConfigProperties;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestTemplate;
import uz.narzullayev.javohir.output.HtmlRender;
import uz.narzullayev.javohir.output.MarkdownRender;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

@AllArgsConstructor
@NoArgsConstructor
public class SwaggerDiffService {
    private Environment environment;
    private SwaggerDiffProperties swaggerDiffProperties;
    private SpringDocConfigProperties springDocConfigProperties;

    @SneakyThrows
    @SuppressWarnings("all")
    public String start() {
        var latestDocPath = swaggerDiffProperties.getLatestDocPath();
        var directory = new File(latestDocPath);
        if (!directory.exists()) {
            directory.mkdir();
        }
        var docFile = "latest_swagger.json";
        var htmlFile = "diff_swagger.html";
        var filePath = Paths.get(directory.getAbsolutePath(), docFile);
        var htmlPath = Paths.get(directory.getAbsolutePath(), htmlFile);
        boolean existFile = Files.exists(filePath);
        String latest;
        String newVersion;
        if (existFile) {
            latest = new String(Files.readAllBytes(filePath));
            newVersion = getDocStr();
            uploadDoc(filePath, newVersion);
        } else {
            latest = getDocStr();
            newVersion = latest;
            uploadDoc(filePath, latest);
        }
        final var diff = OpenApiCompare.fromContents(latest, newVersion);
        var html = new HtmlRender("Changelog",
                "http://deepoove.com/swagger-diff/stylesheets/demo.css")
                .render(diff);
        var mark = new MarkdownRender().render(diff);
        System.out.println(mark);
        Files.writeString(htmlPath, html, StandardCharsets.UTF_8);
        return mark;
    }

    @SneakyThrows
    private static void uploadDoc(Path filePath, String newVersion) {
        var in = new ByteArrayInputStream(newVersion.getBytes());
        Files.copy(in, filePath, StandardCopyOption.REPLACE_EXISTING);
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
