package com.github.vladyslavbabenko.mycoloroflife.controller;

import com.github.vladyslavbabenko.mycoloroflife.entity.dto.ResetPasswordData;
import com.github.vladyslavbabenko.mycoloroflife.service.PasswordRecoveryService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@Controller
@RequestMapping("/password")
public class PasswordRecoveryController {

    private final PasswordRecoveryService passwordRecoveryService;

    @GetMapping("/request")
    public String resetPassword() {
        return "generalTemplate/requestPasswordChange";
    }

    @PatchMapping("/request")
    public String resetPassword(@RequestParam("email") String email, Model model) {
        Pattern emailPattern =
                Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = emailPattern.matcher(email);

        if (matcher.find()) {
            passwordRecoveryService.forgottenPassword(email);
        }

        model.addAttribute("message", "Якщо на цю пошту було зареєстровано обліковий запис, туди буде надіслано посилання для зміни паролю");

        return "generalTemplate/loginPage";
    }

    @GetMapping("/change")
    public String changePassword(@RequestParam(required = false) String token, Model model) {
        if (StringUtils.isEmpty(token)) {
            model.addAttribute("tokenError", "Відсутній токен");
            return "generalTemplate/loginPage";
        }

        model.addAttribute("resetPasswordData", ResetPasswordData.builder().token(token).build());
        return "generalTemplate/changePassword";
    }

    @PostMapping("/change")
    public String changePassword(@ModelAttribute ResetPasswordData data, Model model) {
        if (data.getPassword().equals(data.getPasswordConfirm())) {
            if (passwordRecoveryService.updatePassword(data.getPassword(), data.getToken())) {
                model.addAttribute("message", "Пароль успішно змінений");
            } else {
                model.addAttribute("tokenError", "Неправильний або застарілий токен");
            }
        } else {
            model.addAttribute("passwordMismatchError", "Паролі не співпадають");
            return "generalTemplate/changePassword";
        }

        return "generalTemplate/loginPage";
    }
}