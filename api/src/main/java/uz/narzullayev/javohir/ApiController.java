package uz.narzullayev.javohir;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiController {

    private static final String EXAMPLE_ONE = "{\"glossary\":{\"title\":\"example glossary\",\"GlossDiv\":{\"title\":\"S\",\"GlossList\":{\"GlossEntry\":{\"ID\":\"SGML\",\"SortAs\":\"SGML\",\"GlossTerm\":\"Standard Generalized Markup Language\",\"Acronym\":\"SGML\",\"Abbrev\":\"ISO 8879:1986\",\"GlossDef\":{\"para\":\"A meta-markup language, used to create markup languages such as DocBook.\",\"GlossSeeAlso\":[\"GML\",\"XML\"]},\"GlossSee\":\"markup\"}}}}}";

    private static final String Ex= """
            {
              "id": 57,
              "fio": "test_b35811a3c4e3",
              "car": {
                "fio": "test_3e1fc30791aa"
              }
            }
            """;
    @PostMapping("/student")
    @Operation(responses = {
            @ApiResponse(responseCode = "200", content = @Content(examples = {
                    @ExampleObject(name = "getUserAttribute",
                            summary = "Retrieves a User's attributes.",
                            description = "Retrieves a User's attributes.",
                            value = "[{\"value\": [\"area1\", \"area2\", \"area3\"], \"key\":\"GENERAL_AREAS\"}, {\"value\":\"933933933\", \"key\":\"FONyE\"}]"),
                    @ExampleObject(name = "getStudent",
                            summary = "Retrieves a Student's attributes.",
                            description = "Retrieves a Student's attributes.",
                            value = "[{\"value\": [\"area1\", \"area2\", \"area3\"], \"key\":\"GENERAL_CHANGE\"}, {\"value\":\"933933933\", \"key\":\"FONyE\"}]")
            }, schema = @Schema(implementation = Student.class), mediaType = MediaType.APPLICATION_JSON_VALUE))
    }
    )
    @Tag(name = "Api desc",description = "Description of api")

    public Employer student(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(examples = {
                            @ExampleObject(name = "one", value = EXAMPLE_ONE),
                            @ExampleObject(name = "two", value = Ex),
                            @ExampleObject(name = "three", value = EXAMPLE_ONE)}
                    ))
            @RequestBody Student body
    ) {
        Student student = new Student();
        //student.setId(1);
        student.setFio("Narzullayev Javohir");
        return student;
    }

    @GetMapping
    public String tes(){
        return "";
    }
}
