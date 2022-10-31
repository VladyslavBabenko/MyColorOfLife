package com.github.vladyslavbabenko.mycoloroflife.controller;

import com.github.vladyslavbabenko.mycoloroflife.util.MessageSourceUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * {@link Controller} for general pages.
 */

@Controller
@RequiredArgsConstructor
public class GeneralController {

    private final MessageSourceUtil messageSource;

    @GetMapping()
    public String getMainPage() {
        return messageSource.getMessage("template.general.main");
    }

    @GetMapping("/login")
    public String getLogin() {
        return messageSource.getMessage("template.general.login");
    }

    @GetMapping("/access-denied")
    public String getAccessDeniedPage() {
        return messageSource.getMessage("template.error.access-denied");
    }
}