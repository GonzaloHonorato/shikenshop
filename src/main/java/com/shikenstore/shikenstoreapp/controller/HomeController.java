package com.shikenstore.shikenstoreapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping({"/", "/inicio"})
    public String inicio(Model model) {
        return "inicio";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
