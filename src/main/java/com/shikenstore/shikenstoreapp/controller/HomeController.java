package com.shikenstore.shikenstoreapp.controller;

import com.shikenstore.shikenstoreapp.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final ProductService productService;

    public HomeController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping({"/", "/inicio"})
    public String inicio(Model model) {
        model.addAttribute("featured", productService.getFeatured());
        model.addAttribute("products", productService.getAllActive());
        return "inicio";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
