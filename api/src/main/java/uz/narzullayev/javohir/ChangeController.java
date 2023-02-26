package uz.narzullayev.javohir;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.nio.file.Files;


@Controller
public class ChangeController {
    @Autowired
    private Environment environment;

    @GetMapping("/changelog")
    @ResponseBody
    public String change() {
        try {
            var file = ResourceUtils.getFile(
                    "api/target/changelog.html"
            );
            return new String(Files.readAllBytes(file.toPath()));
        } catch (IOException ignored) {
            return null;
        }
    }
}
