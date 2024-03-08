package lemonsoft.senac.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class HomeController {
    
    @RequestMapping("/")
    public String loginBackOffice(Model model) {
        return "index";
    }

    @RequestMapping("/home")
    public String homeCliente(Model model) {
        model.addAttribute("msg", "Seja bem-vindo ao LemonSoft_.");
        return "client/index";
    }

}
