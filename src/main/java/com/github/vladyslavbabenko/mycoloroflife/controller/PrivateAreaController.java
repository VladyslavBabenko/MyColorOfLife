package com.github.vladyslavbabenko.mycoloroflife.controller;

import com.github.vladyslavbabenko.mycoloroflife.entity.ActivationCode;
import com.github.vladyslavbabenko.mycoloroflife.entity.CourseTitle;
import com.github.vladyslavbabenko.mycoloroflife.entity.Role;
import com.github.vladyslavbabenko.mycoloroflife.entity.User;
import com.github.vladyslavbabenko.mycoloroflife.enumeration.UserRegistrationType;
import com.github.vladyslavbabenko.mycoloroflife.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@RequiredArgsConstructor
@Controller
@RequestMapping("/me")
public class PrivateAreaController {
    private final UserService userService;
    private final ActivationCodeService activationCodeService;
    private final MailSenderService mailSender;
    private final CourseTitleService courseTitleService;
    private final RoleService roleService;

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

    @PutMapping("/activate-code")
    public String activateCode(@RequestParam("activationCode") String activationCodeAsString, Model model) {
        if (activationCodeAsString != null && !activationCodeAsString.isEmpty()) {
            Optional<ActivationCode> code = activationCodeService.findByCode(activationCodeAsString);
            if (code.isEmpty()) {
                model.addAttribute("user", userService.getCurrentUser());
                model.addAttribute("activationCodeError", "Недійсний код активації");
                return "userTemplate/privateAreaPage";
            }

            code.ifPresent(userService::activateCode);
        }
        return "redirect:/me";
    }

    //Temp
    @PostMapping("/generate/activation-code")
    private String selfGenerateCode(Model model) {
        Optional<CourseTitle> courseTitleFromDB = courseTitleService.findByTitle("Test Course");
        User currentUser = userService.getCurrentUser();
        if (courseTitleFromDB.isPresent()) {
            Optional<Role> roleFromDB = roleService.findByRoleName("ROLE_COURSE_OWNER_" + roleService.convertToRoleStyle(courseTitleFromDB.get().getTitle()));
            if (roleFromDB.isPresent()) {
                if (activationCodeService.existsByUser(currentUser) || currentUser.getRoles().contains(roleFromDB.get())) {
                    model.addAttribute("codeAlreadyGeneratedError", "Ви вже згенерували тестовий код активації");
                } else {
                    ActivationCode code = activationCodeService.createCode(courseTitleFromDB.get(), currentUser);
                    String htmlText = "<div style=\"text-align:center;line-height:24px\"> " +
                            "<span style=\"font-size:25px;\">" +
                            "<p>Здрастуйте, " + currentUser.getUsername() + "!</p>" +
                            "<p> " +
                            "Дякуємо за генерацію коду для курсу " + code.getCourseTitle().getTitle() +
                            "</p>" +
                            "<p style = \"font-size:35px;line-height:40px;font-weight:bold\">" +
                            "Ваш код активації: <br>" + code.getCode() +
                            "</p>" +
                            "</div>";

                    mailSender.sendEmail(currentUser.getEmail(),
                            "Код активації для курсу " + code.getCourseTitle().getTitle(),
                            htmlText);

                    model.addAttribute("mailHasBeenSent", "Код активації було надіслано на Вашу пошту");
                }
            }
        }
        model.addAttribute("user", currentUser);
        return "userTemplate/privateAreaPage";
    }
}