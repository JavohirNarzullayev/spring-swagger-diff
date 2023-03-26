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

    @GetMapping("/v1")
    public String test2(

    ){
        return "Hello";
    }

    @GetMapping("/v21")
    @Deprecated
    public String test22(
        @RequestParam String add
    ){
        return "Hello";
    }


    @PostMapping
    public String testPost(){
        return "Hello post";
    }
}
