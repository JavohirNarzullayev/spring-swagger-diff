package uz.narzullayev.javohir;

import org.springframework.web.bind.annotation.*;

@RestController
public class ApiController {
    @GetMapping("/v1/admin/wanteds/{id}")
    public String test(
            @RequestParam Integer size
    ){
        return "Hello";
    }

    @PostMapping
    public String testPost(){
        return "Hello post";
    }
}
