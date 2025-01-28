package challenges.challenge02_todolist.controllers;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/hello")
    public String hello(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("username", user.getUsername());
        return "hello";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
