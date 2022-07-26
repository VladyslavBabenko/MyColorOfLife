package com.github.vladyslavbabenko.mycoloroflife.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GeneralController {

    @GetMapping()
    public String toMainPage() {
        return "generalTemplate/mainPage";
    }

    @GetMapping("/login")
    public String login() {
        return "generalTemplate/loginPage";
    }

    @GetMapping("/access-denied")
    public String toAccessDeniedPage() {
        return "exceptionTemplate/exceptionPage";
    }
}