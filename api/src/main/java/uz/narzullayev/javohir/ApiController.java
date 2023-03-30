package uz.narzullayev.javohir;

import io.swagger.v3.oas.annotations.Operation;
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
    ){
        return "Hello";
    }

    @GetMapping("/123123123")
    @Operation(summary = "test summary2")
    public TEST TEST(
            @RequestParam String add,
            @RequestParam String add1
    ){
        return new TEST();
    }

    @PostMapping
    public Integer testPost(
            @RequestBody Integer s
    ){
        return 2;
    }
}
