package com.github.vladyslavbabenko.mycoloroflife.controller;

import com.github.vladyslavbabenko.mycoloroflife.entity.dto.ResetPasswordData;
import com.github.vladyslavbabenko.mycoloroflife.service.PasswordRecoveryService;
import com.github.vladyslavbabenko.mycoloroflife.util.MessageSourceUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * {@link Controller} to manage user password recovery.
 */

@Controller
@RequiredArgsConstructor
@RequestMapping("/password")
public class PasswordRecoveryController {

    private final PasswordRecoveryService passwordRecoveryService;
    private final MessageSourceUtil messageSource;

    @GetMapping("/request")
    public String getResetPassword() {
        return messageSource.getMessage("template.general.request.password");
    }

    @PatchMapping("/request")
    public String patchResetPassword(@RequestParam("email") String email, Model model) {
        Pattern emailPattern =
                Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = emailPattern.matcher(email);

        if (matcher.find()) {
            passwordRecoveryService.forgottenPassword(email);
        }

        model.addAttribute("message", messageSource.getMessage("user.password.recovery.message"));

        return messageSource.getMessage("template.general.login");
    }

    @GetMapping("/change")
    public String getChangePassword(@RequestParam(required = false) String token, Model model) {
        if (StringUtils.isEmpty(token)) {
            model.addAttribute("tokenError", messageSource.getMessage("empty.token"));
            return messageSource.getMessage("template.general.login");
        }

        model.addAttribute("resetPasswordData", ResetPasswordData.builder().token(token).build());
        return messageSource.getMessage("template.general.edit.password");
    }

    @PostMapping("/change")
    public String postChangePassword(@ModelAttribute ResetPasswordData data, Model model) {
        if (data.getPassword().equals(data.getPasswordConfirm())) {
            if (passwordRecoveryService.updatePassword(data.getPassword(), data.getToken())) {
                model.addAttribute("message", messageSource.getMessage("user.password.updated"));
            } else {
                model.addAttribute("tokenError", messageSource.getMessage("invalid.token"));
            }
        } else {
            model.addAttribute("passwordMismatchError", messageSource.getMessage("user.password.mismatch"));
            return messageSource.getMessage("template.general.edit.password");
        }

        return messageSource.getMessage("template.general.login");
    }
}