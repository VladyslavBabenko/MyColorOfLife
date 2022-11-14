package com.github.vladyslavbabenko.mycoloroflife.service.implementation;

import com.github.vladyslavbabenko.mycoloroflife.entity.SecureToken;
import com.github.vladyslavbabenko.mycoloroflife.entity.User;
import com.github.vladyslavbabenko.mycoloroflife.enumeration.Purpose;
import com.github.vladyslavbabenko.mycoloroflife.service.*;
import com.github.vladyslavbabenko.mycoloroflife.util.MessageSourceUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of {@link PasswordRecoveryService}.
 */

@Service
public class PasswordRecoveryServiceImpl implements PasswordRecoveryService {
    private final UserService userService;
    private final SecureTokenService secureTokenService;
    private final MailSenderService mailSenderService;
    private final MailContentBuilderService mailContentBuilder;
    private final MessageSourceUtil messageSource;

    @Value("${site.base.url.https}")
    private String baseURL;

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    public PasswordRecoveryServiceImpl(UserService userService,
                                       SecureTokenService secureTokenService,
                                       MailSenderService mailSenderService,
                                       MailContentBuilderService mailContentBuilder,
                                       MessageSourceUtil messageSource) {
        this.userService = userService;
        this.secureTokenService = secureTokenService;
        this.mailSenderService = mailSenderService;
        this.mailContentBuilder = mailContentBuilder;
        this.messageSource = messageSource;
    }

    @Override
    public boolean forgottenPassword(String username) {
        try {
            User userFromDB = (User) userService.loadUserByUsername(username);
            sendResetPasswordEmail(userFromDB);
            return true;
        } catch (UsernameNotFoundException e) {
            log.warn("UsernameNotFoundException in {} : {}", this.getClass().getSimpleName(), e.getMessage());
            return false;
        }
    }

    @Override
    public boolean updatePassword(String password, String token) {
        Optional<SecureToken> secureToken = secureTokenService.findByToken(token);

        if (secureToken.isEmpty() || !StringUtils.equals(token, secureToken.get().getToken()) || secureToken.get().isExpired()) {
            log.info("Invalid secureToken parameters");
            return false;
        }

        User userFromDB;

        try {
            userFromDB = (User) userService.loadUserByUsername(secureToken.get().getUser().getUsername());
        } catch (UsernameNotFoundException e) {
            log.warn("UsernameNotFoundException in {} : {}", this.getClass().getSimpleName(), e.getMessage());
            return false;
        }

        userFromDB.setPassword(userService.encodePassword(password));
        userFromDB.setAccountNonLocked(true);
        userFromDB.setFailedLoginAttempt(0);

        userService.updateUser(userFromDB);
        secureTokenService.delete(secureToken.get());

        log.info("Password updated for user with username {}", userFromDB.getUsername());

        return true;
    }

    protected void sendResetPasswordEmail(User user) {
        SecureToken secureToken = secureTokenService.createSecureToken();
        secureToken.setUser(user);
        secureToken.setPurpose(Purpose.PASSWORD_RECOVERY);
        secureTokenService.update(secureToken);

        List<String> strings;

        switch (user.getRegistrationType()) {
            case REGISTRATION_FORM:
                strings = new ArrayList<>();
                strings.add(user.getName());
                strings.add(messageSource.getMessage("email.reset.password.message.to.link"));
                strings.add(getResetPasswordUrl(secureToken.getToken()));
                mailSenderService.sendEmail(user.getEmail(),
                        messageSource.getMessage("email.reset.password.subject"),
                        mailContentBuilder.build(strings, messageSource.getMessage("template.email.password.forgot.registration-form")));
                break;

            case GMAIL_AUTHENTICATION:
                strings = new ArrayList<>();
                strings.add(user.getName());
                strings.add(messageSource.getMessage("email.gmail.login.text"));
                mailSenderService.sendEmail(user.getEmail(),
                        messageSource.getMessage("email.reset.password.subject"),
                        mailContentBuilder.build(strings, messageSource.getMessage("template.email.password.forgot.gmail-authentication")));
                break;
        }

        log.info("Password reset email sent to - {}", user.getEmail());
    }

    protected String getResetPasswordUrl(String token) {
        return UriComponentsBuilder.fromHttpUrl(baseURL).path("/password/change").queryParam("token", token).toUriString();
    }

    @Override
    public boolean loginDisabled(String username) {
        try {
            User userFromDB = (User) userService.loadUserByUsername(username);
            if (userFromDB != null && !userFromDB.isAccountNonLocked()) {
                sendResetPasswordEmail(userFromDB);
                return true;
            } else return false;
        } catch (UsernameNotFoundException e) {
            log.warn("UsernameNotFoundException in {} : {}", this.getClass().getSimpleName(), e.getMessage());
            return false;
        }
    }
}