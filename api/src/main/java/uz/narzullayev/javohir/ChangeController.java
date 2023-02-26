package uz.narzullayev.javohir;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ChangeController {

    @GetMapping("/changelog")
    @ResponseBody
    public String change(){
        return "";
    }
}
