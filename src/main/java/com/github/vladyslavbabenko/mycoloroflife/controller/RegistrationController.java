package com.github.vladyslavbabenko.mycoloroflife.controller;

import com.github.vladyslavbabenko.mycoloroflife.entity.User;
import com.github.vladyslavbabenko.mycoloroflife.service.UserService;
import com.github.vladyslavbabenko.mycoloroflife.util.MessageSourceUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

/**
 * {@link Controller} to manage user registration.
 */

@Controller
@RequiredArgsConstructor
public class RegistrationController {
    private final UserService userService;
    private final MessageSourceUtil messageSource;

    @GetMapping("/registration")
    public String getRegistration(Model model) {
        model.addAttribute("user", new User());
        return messageSource.getMessage("template.general.registration");
    }

    @PostMapping("/registration")
    public String registerNewUser(@ModelAttribute @Valid User user, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            return messageSource.getMessage("template.general.registration");
        }

        if (!user.getPassword().equals(user.getPasswordConfirm())) {
            model.addAttribute("passwordMismatchError", messageSource.getMessage("user.password.mismatch"));
            return messageSource.getMessage("template.general.registration");
        }

        if (!userService.saveUser(user)) {
            model.addAttribute("saveUserError", messageSource.getMessage("user.exists.already"));
            return messageSource.getMessage("template.general.registration");
        }

        return "redirect:/";
    }
}