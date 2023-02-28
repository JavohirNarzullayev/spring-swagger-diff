package uz.narzullayev.javohir;

import org.springframework.web.bind.annotation.*;

@RestController
public class ApiController {
  /*  @GetMapping("/v1/admin/wanteds/{id}")
    public String test(
            @RequestParam Integer size,
            @PathVariable String id
    ){
        return "Hello";
    }
*/
    @PostMapping
    public String testPost(){
        return "Hello post";
    }
}
