package com.github.vladyslavbabenko.mycoloroflife.controller;

import com.github.vladyslavbabenko.mycoloroflife.entity.User;
import com.github.vladyslavbabenko.mycoloroflife.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@RequiredArgsConstructor
@Controller
@RequestMapping("/me")
public class PrivateAreaController {
    private final UserService userService;

    @GetMapping()
    public String toPersonalArea(Model model) {
        model.addAttribute("userFromDB", userService.getCurrentUser());
        return "userTemplate/privateAreaPage";
    }

    @GetMapping("/change-password")
    public String toChangePasswordPage(Model model) {
        model.addAttribute("user", userService.getCurrentUser());
        return "userTemplate/changePasswordPage";
    }

    @PatchMapping("/change-password")
    public String changePassword(@ModelAttribute User user, Model model) {
        user.setId(userService.getCurrentUser().getId());

        if (user.getPassword().length() < 5 || user.getPassword().length() > 30) {
            model.addAttribute("passwordOutOfBounds", "Довжина пароля має бути від 5 до 30 символів.");
            return "userTemplate/changePasswordPage";
        }

        if (!user.getPassword().equals(user.getPasswordConfirm())) {
            model.addAttribute("passwordConfirmError", "Паролі не співпадають.");
            return "userTemplate/changePasswordPage";
        }

        userService.changePassword(user);

        return "redirect:/me";
    }

    @GetMapping("/edit")
    public String toEditPage(Model model) {
        model.addAttribute("user", userService.getCurrentUser());
        return "userTemplate/editPage";
    }

    @PatchMapping("/edit")
    public String updateUser(@ModelAttribute @Valid User user, BindingResult bindingResult, Model model) {
        user.setId(userService.getCurrentUser().getId());

        if (bindingResult.hasErrors()) {
            return "userTemplate/editPage";
        }

        if (!userService.updateUser(user)) {
            model.addAttribute("usernameError", "Ця пошта вже зайнята.");
            return "userTemplate/editPage";
        }
        return "redirect:/me";
    }
}