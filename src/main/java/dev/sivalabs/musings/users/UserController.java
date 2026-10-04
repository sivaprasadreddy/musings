package dev.sivalabs.musings.users;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
class UserController {

    @GetMapping("/login")
    String loginForm() {
        return "login";
    }
}
