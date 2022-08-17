package com.github.vladyslavbabenko.mycoloroflife.controller;

import com.github.vladyslavbabenko.mycoloroflife.entity.User;
import com.github.vladyslavbabenko.mycoloroflife.enumeration.UserRegistrationType;
import com.github.vladyslavbabenko.mycoloroflife.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequiredArgsConstructor
@Controller
@RequestMapping("/me")
public class PrivateAreaController {
    private final UserService userService;

    @GetMapping()
    public String toPersonalArea(Model model) {
        model.addAttribute("user", userService.getCurrentUser());
        return "userTemplate/privateAreaPage";
    }

    @GetMapping("/change-password")
    public String toChangePasswordPage(Model model) {
        if (userService.getCurrentUser().getRegistrationType() == UserRegistrationType.REGISTRATION_FORM) {
            model.addAttribute("user", userService.getCurrentUser());
            return "userTemplate/changePasswordPage";
        }
        return "redirect:/me";
    }

    @PatchMapping("/change-password")
    public String changePassword(@ModelAttribute User userToUpdate, @RequestParam String oldPassword, Model model) {
        userToUpdate.setId(userService.getCurrentUser().getId());

        if (userService.getCurrentUser().getRegistrationType() == UserRegistrationType.REGISTRATION_FORM) {
            userToUpdate.setId(userService.getCurrentUser().getId());

            if (userService.matchesPassword(userService.getCurrentUser(), oldPassword)) {
                if (userToUpdate.getPassword().length() < 5 || userToUpdate.getPassword().length() > 30) {
                    if (userToUpdate.getPassword().length() != 0) {
                        model.addAttribute("passwordOutOfBounds", "Довжина пароля має бути від 5 до 30 символів");
                        return "userTemplate/changePasswordPage";
                    } else {
                        return "redirect:/me";
                    }
                }
            } else {
                if (userService.getCurrentUser().equals(new User()) || userService.getCurrentUser().getId() == null) {
                    model.addAttribute("changePasswordError", "Користувач не знайдений");
                } else {
                    model.addAttribute("oldPasswordError", "Невірний пароль");
                }
                return "userTemplate/changePasswordPage";
            }

            if (!userToUpdate.getPassword().equals(userToUpdate.getPasswordConfirm())) {
                model.addAttribute("passwordMismatchError", "Паролі не співпадають");
                return "userTemplate/changePasswordPage";
            }

            userService.changePassword(userToUpdate);
        }

        return "redirect:/me";
    }

    @GetMapping("/edit")
    public String toEditPage(Model model) {
        if (userService.getCurrentUser().getRegistrationType() == UserRegistrationType.REGISTRATION_FORM) {
            model.addAttribute("user", userService.getCurrentUser());
            return "userTemplate/editPage";
        }
        return "redirect:/me";
    }

    @PatchMapping("/edit")
    public String updateUser(@ModelAttribute @Valid User userToUpdate, BindingResult bindingResult, Model model) {
        userToUpdate.setId(userService.getCurrentUser().getId());
        if (userService.getCurrentUser().getRegistrationType() == UserRegistrationType.REGISTRATION_FORM) {
            if (bindingResult.hasFieldErrors("email")) {
                return "userTemplate/editPage";
            }
            userService.updateUser(userToUpdate);
        }

        if (!userService.existsById(userToUpdate.getId())) {
            model.addAttribute("updateUserError", "Користувач не знайдений");
            return "userTemplate/editPage";
        }

        return "redirect:/me";
    }
}