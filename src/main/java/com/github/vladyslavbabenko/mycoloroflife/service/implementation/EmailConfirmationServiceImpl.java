package com.github.vladyslavbabenko.mycoloroflife.service.implementation;

import com.github.vladyslavbabenko.mycoloroflife.entity.SecureToken;
import com.github.vladyslavbabenko.mycoloroflife.entity.User;
import com.github.vladyslavbabenko.mycoloroflife.enumeration.Purpose;
import com.github.vladyslavbabenko.mycoloroflife.service.*;
import com.github.vladyslavbabenko.mycoloroflife.util.MessageSourceUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class EmailConfirmationServiceImpl implements EmailConfirmationService {

    private final UserService userService;
    private final SecureTokenService secureTokenService;
    private final MailSenderService mailSenderService;
    private final MailContentBuilderService mailContentBuilder;
    private final MessageSourceUtil messageSource;

    @Value("${site.base.url.https}")
    private String baseURL;

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public EmailConfirmationServiceImpl(UserService userService,
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
    public boolean confirmEmail(String token) {
        Optional<SecureToken> secureToken = secureTokenService.findByToken(token);
        if (secureToken.isEmpty() || !StringUtils.equals(token, secureToken.get().getToken())) {
            log.info("Invalid secureToken parameters");
            return false;
        }

        if (secureToken.get().isExpired()) {
            secureTokenService.delete(secureToken.get());
            log.info("{} has been deleted", secureToken.get().getToken());
            return false;
        }

        try {
            userService.confirmEmail(secureToken.get().getUser().getUsername());
            secureTokenService.delete(secureToken.get());
            return true;
        } catch (UsernameNotFoundException e) {
            log.warn("UsernameNotFoundException in {} : {}", this.getClass().getSimpleName(), e.getMessage());
            return false;
        }
    }

    public void sendConfirmationEmail(User user) {
        SecureToken secureToken = secureTokenService.createSecureToken();
        secureToken.setUser(user);
        secureToken.setPurpose(Purpose.EMAIL_CONFIRM);
        secureTokenService.update(secureToken);

        List<String> strings;

        strings = new ArrayList<>();
        strings.add(user.getName());
        strings.add(messageSource.getMessage("email.confirm.text"));
        strings.add(getConfirmationEmailUrl(secureToken.getToken()));
        mailSenderService.sendEmail(user.getEmail(),
                messageSource.getMessage("email.confirm.subject"),
                mailContentBuilder.build(strings, messageSource.getMessage("template.email.confirm")));

        log.info("Confirmation email sent to - {}", user.getEmail());
    }

    protected String getConfirmationEmailUrl(String token) {
        return UriComponentsBuilder.fromHttpUrl(baseURL).path("/me/email-confirm").queryParam("token", token).toUriString();
    }
}