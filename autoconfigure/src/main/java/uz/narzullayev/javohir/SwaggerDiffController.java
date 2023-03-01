package uz.narzullayev.javohir;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.nio.file.Files;

@Slf4j
@Controller
public class SwaggerDiffController {

    /**
     * The Swagger welcome common.
     */
    public static final String SWAGGER_UI_PATH = "${springdoc.swagger-ui..diff.path-web}";

    private final SwaggerDiffProperties swaggerDiffProperties;

    /**
     * Instantiates a new Swagger config resource.
     *
     * @param swaggerWelcomeCommon the swagger welcome common
     */
    public SwaggerDiffController(SwaggerDiffProperties swaggerWelcomeCommon) {
        this.swaggerDiffProperties = swaggerWelcomeCommon;
    }


    @Operation(hidden = true)
    @GetMapping(value = SWAGGER_UI_PATH)
    @ResponseBody
    public String diffHtml() {
        try {
            var latestDocPath = swaggerDiffProperties.getLatestDocPath();
            var html = latestDocPath +"/"+ "diff_swagger.html";
            var file = ResourceUtils.getFile(html);
            log.info("Swagger ui path difference : {}",SWAGGER_UI_PATH);
            return new String(Files.readAllBytes(file.toPath()));
        } catch (IOException ignored) {
            return null;
        }
    }

}

