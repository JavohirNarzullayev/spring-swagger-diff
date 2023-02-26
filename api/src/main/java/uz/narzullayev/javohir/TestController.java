package uz.narzullayev.javohir;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @GetMapping("/test")
    public String test(){
        return "Hello";
    }

    @PostMapping("/test")
    public String testPost(){
        return "Hello post";
    }
}
