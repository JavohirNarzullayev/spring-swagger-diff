package uz.narzullayev.javohir;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiController {
    @GetMapping
    public String test(){
        return "Hello";
    }

    @PostMapping
    public String testPost(){
        return "Hello post";
    }
}
