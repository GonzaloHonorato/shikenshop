package com.shikenstore.shikenstoreapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * SPA fallback controller.
 * Forwards all non-API, non-static routes to Angular's index.html.
 * When Angular is built and placed in src/main/resources/static/,
 * this controller enables client-side routing.
 */
@Controller
public class HomeController {

    @GetMapping(value = {
        "/",
        "/{path:^(?!api|swagger-ui|v3|assets|.*\\..*).*$}",
        "/{path:^(?!api|swagger-ui|v3|assets|.*\\..*).*$}/**"
    })
    public String forward() {
        return "forward:/index.html";
    }
}
