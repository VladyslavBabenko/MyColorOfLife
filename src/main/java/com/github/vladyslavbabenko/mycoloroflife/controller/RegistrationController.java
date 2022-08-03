package com.github.vladyslavbabenko.mycoloroflife.controller;

import com.github.vladyslavbabenko.mycoloroflife.entity.User;
import com.github.vladyslavbabenko.mycoloroflife.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@AllArgsConstructor
@Controller
public class RegistrationController {
    private final UserService userService;

    @GetMapping("/registration")
    public String toRegistrationPage(Model model) {
        model.addAttribute("user", new User());
        return "generalTemplate/registrationPage";
    }

    @PostMapping("/registration")
    public String registerNewUser(@ModelAttribute @Valid User user, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            return "generalTemplate/registrationPage";
        }

        if (!user.getPassword().equals(user.getPasswordConfirm())) {
            model.addAttribute("passwordMismatchError", "Паролі не співпадають");
            return "generalTemplate/registrationPage";
        }

        if (!userService.saveUser(user)) {
            model.addAttribute("saveUserError", "Цей користувач уже існує");
            return "generalTemplate/registrationPage";
        }

        return "redirect:/";
    }
}