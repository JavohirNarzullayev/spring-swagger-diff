package uz.narzullayev.javohir;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.*;

@RestController
public class ApiController {

    @GetMapping("/student")
    @Operation(summary = "Information of student",description = "Description of student")
    public Student student(){
        Student student = new Student();
        student.setId(1);
        student.setFio("Narzullayev Javohir");
        return student;
    }
}
