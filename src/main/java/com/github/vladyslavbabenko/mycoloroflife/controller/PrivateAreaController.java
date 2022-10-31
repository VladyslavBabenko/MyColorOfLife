package com.github.vladyslavbabenko.mycoloroflife.controller;

import com.github.vladyslavbabenko.mycoloroflife.entity.*;
import com.github.vladyslavbabenko.mycoloroflife.enumeration.Purpose;
import com.github.vladyslavbabenko.mycoloroflife.enumeration.UserRegistrationType;
import com.github.vladyslavbabenko.mycoloroflife.service.*;
import com.github.vladyslavbabenko.mycoloroflife.util.MessageSourceUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * {@link Controller} for user private area.
 */

@Controller
@RequiredArgsConstructor
@RequestMapping("/me")
public class PrivateAreaController {

    private final RoleService roleService;
    private final UserService userService;
    private final MailSenderService mailSender;
    private final MessageSourceUtil messageSource;
    private final SecureTokenService secureTokenService;
    private final CourseTitleService courseTitleService;
    private final ActivationCodeService activationCodeService;
    private final EmailConfirmationService emailConfirmationService;
    private final MailContentBuilderService mailContentBuilderService;

    private final String REDIRECT_ME = "redirect:/me";

    @GetMapping()
    public String getPersonalArea(Model model) {
        model.addAttribute("user", userService.getCurrentUser());
        return messageSource.getMessage("template.user.private-area");
    }

    @GetMapping("/change-password")
    public String getChangePassword(Model model) {
        if (userService.getCurrentUser().getRegistrationType() == UserRegistrationType.REGISTRATION_FORM) {
            model.addAttribute("user", userService.getCurrentUser());
            return messageSource.getMessage("template.user.edit.password");
        }
        return REDIRECT_ME;
    }

    @PatchMapping("/change-password")
    public String patchChangePassword(@ModelAttribute User userToUpdate, @RequestParam String oldPassword, Model model) {
        userToUpdate.setId(userService.getCurrentUser().getId());

        if (userService.getCurrentUser().getRegistrationType() == UserRegistrationType.REGISTRATION_FORM) {
            userToUpdate.setId(userService.getCurrentUser().getId());

            if (userService.matchesPassword(userService.getCurrentUser(), oldPassword)) {
                if (userToUpdate.getPassword().length() < 5 || userToUpdate.getPassword().length() > 30) {
                    if (userToUpdate.getPassword().length() != 0) {
                        model.addAttribute("passwordOutOfBounds", messageSource.getMessage("user.password.length"));
                        return messageSource.getMessage("template.user.edit.password");
                    } else {
                        return REDIRECT_ME;
                    }
                }
            } else {
                if (userService.getCurrentUser().equals(new User()) || userService.getCurrentUser().getId() == null) {
                    model.addAttribute("changePasswordError", messageSource.getMessage("user.not.found"));
                } else {
                    model.addAttribute("oldPasswordError", messageSource.getMessage("user.password.invalid"));
                }
                return messageSource.getMessage("template.user.edit.password");
            }

            if (!userToUpdate.getPassword().equals(userToUpdate.getPasswordConfirm())) {
                model.addAttribute("passwordMismatchError", messageSource.getMessage("user.password.mismatch"));
                return messageSource.getMessage("template.user.edit.password");
            }

            userService.changePassword(userToUpdate);
        }

        return REDIRECT_ME;
    }

    @GetMapping("/edit")
    public String getEdit(Model model) {
        if (userService.getCurrentUser().getRegistrationType() == UserRegistrationType.REGISTRATION_FORM) {
            model.addAttribute("user", userService.getCurrentUser());
            return messageSource.getMessage("template.user.edit.details");
        }
        return REDIRECT_ME;
    }

    @PatchMapping("/edit")
    public String patchEdit(@ModelAttribute @Valid User userToUpdate, BindingResult bindingResult, Model model) {
        userToUpdate.setId(userService.getCurrentUser().getId());
        if (userService.getCurrentUser().getRegistrationType() == UserRegistrationType.REGISTRATION_FORM) {
            if (bindingResult.hasFieldErrors("email")) {
                return messageSource.getMessage("template.user.edit.details");
            }
            userService.updateUser(userToUpdate);
        }

        if (!userService.existsById(userToUpdate.getId())) {
            model.addAttribute("updateUserError", messageSource.getMessage("user.not.found"));
            return messageSource.getMessage("template.user.edit.details");
        }

        return REDIRECT_ME;
    }

    @PutMapping("/activate-code")
    public String putActivateCode(@RequestParam("activationCode") String activationCodeAsString, Model model) {
        if (activationCodeAsString != null && !activationCodeAsString.isEmpty()) {
            Optional<ActivationCode> code = activationCodeService.findByCode(activationCodeAsString);
            if (code.isEmpty()) {
                model.addAttribute("user", userService.getCurrentUser());
                model.addAttribute("activationCodeError", messageSource.getMessage("user.invalid.activation-code"));
                return messageSource.getMessage("template.user.private-area");
            }

            code.ifPresent(userService::activateCode);
        }
        return REDIRECT_ME;
    }

    //Temp
    @PostMapping("/generate/activation-code")
    private String postSelfGenerateCode(Model model) {

        Optional<CourseTitle> courseTitleFromDB = courseTitleService.findByTitle(messageSource.getMessage("test.course.title"));
        User currentUser = userService.getCurrentUser();

        if (!currentUser.isEmailConfirmed()) {
            model.addAttribute("message", messageSource.getMessage("user.activation-code.email.confirm"));
            return getPersonalArea(model);
        }

        if (courseTitleFromDB.isPresent()) {
            Optional<Role> roleFromDB =
                    roleService.findByRoleName(messageSource.getMessage("role.course.owner") + roleService.convertToRoleStyle(courseTitleFromDB.get().getTitle()));
            if (roleFromDB.isPresent()) {
                if (activationCodeService.existsByUser(currentUser) || currentUser.getRoles().contains(roleFromDB.get())) {
                    model.addAttribute("codeAlreadyGeneratedError", messageSource.getMessage("user.activation-code.already.generated"));
                } else {
                    ActivationCode code = activationCodeService.createCode(courseTitleFromDB.get(), currentUser);

                    String courseTitle = code.getCourseTitle().getTitle();
                    String subject = messageSource.getMessage("email.course.activation-code.subject") + " " + courseTitle;

                    List<String> strings = new ArrayList<>();
                    strings.add(currentUser.getName());
                    strings.add(courseTitle);
                    strings.add(code.getCode());

                    mailSender.sendEmail(currentUser.getEmail(), subject,
                            mailContentBuilderService.build(strings, messageSource.getMessage("template.email.activation-code")));

                    model.addAttribute("mailHasBeenSent", messageSource.getMessage("user.activation-code.email.sent"));
                }
            }
        }

        return getPersonalArea(model);
    }

    @PostMapping("/email-request")
    private String postEmailRequest(Model model) {
        User currentUser = userService.getCurrentUser();
        if (!currentUser.isEmailConfirmed() && !secureTokenService.existsByUserAndPurpose(currentUser, Purpose.EMAIL_CONFIRM)) {
            emailConfirmationService.sendConfirmationEmail(userService.getCurrentUser());
        }

        model.addAttribute("message", messageSource.getMessage("user.email.confirm.sent"));

        return getPersonalArea(model);
    }

    @GetMapping("/email-confirm")
    private String getEmailConfirm(@RequestParam(required = false) String token, Model model) {
        if (StringUtils.isEmpty(token)) {
            model.addAttribute("tokenError", messageSource.getMessage("empty.token"));
            return messageSource.getMessage("template.general.email.confirm");
        }

        Optional<SecureToken> secureTokenFromDB = secureTokenService.findByToken(token);

        if (secureTokenFromDB.isEmpty()) {
            model.addAttribute("tokenError", messageSource.getMessage("invalid.token"));
        } else {
            model.addAttribute("token", secureTokenFromDB.get().getToken());
            model.addAttribute("email", secureTokenFromDB.get().getUser().getEmail());
        }

        return messageSource.getMessage("template.general.email.confirm");
    }

    @PostMapping("/email-confirm")
    private String postEmailConfirm(@RequestParam(required = false) String token, Model model) {
        if (emailConfirmationService.confirmEmail(token)) {
            model.addAttribute("message", messageSource.getMessage("user.email.confirm.success"));
        } else {
            model.addAttribute("message", messageSource.getMessage("invalid.token"));
        }

        return getPersonalArea(model);
    }
}