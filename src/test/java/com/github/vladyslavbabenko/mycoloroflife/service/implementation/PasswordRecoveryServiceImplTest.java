package com.github.vladyslavbabenko.mycoloroflife.service.implementation;

import com.github.vladyslavbabenko.mycoloroflife.entity.SecureToken;
import com.github.vladyslavbabenko.mycoloroflife.entity.User;
import com.github.vladyslavbabenko.mycoloroflife.enumeration.UserRegistrationType;
import com.github.vladyslavbabenko.mycoloroflife.service.MailContentBuilderService;
import com.github.vladyslavbabenko.mycoloroflife.service.MailSenderService;
import com.github.vladyslavbabenko.mycoloroflife.service.SecureTokenService;
import com.github.vladyslavbabenko.mycoloroflife.service.UserService;
import org.fest.assertions.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@TestPropertySource(value = {"classpath:application-test.properties", "classpath:messages.properties"})
@DisplayName("Unit-level testing for PasswordRecoveryServiceImpl")
class PasswordRecoveryServiceImplTest {

    private UserService userService;
    private SecureTokenService secureTokenService;
    private MailSenderService mailSenderService;
    private MailContentBuilderService mailContentBuilder;
    private MessageSource messageSource;

    private PasswordRecoveryServiceImpl passwordRecoveryService;

    private SecureToken expectedSecureToken;
    private User expectedUser, expectedUserGAuth;

    @Value("${site.base.url.http}")
    private String baseURL;
    @Value("${secure.token.validity}")
    private int tokenValidityInSeconds;
    @Value("${email.reset.password.message.to.link}")
    private String emailResetPasswordMessageToLink;
    @Value("${email.reset.password.subject}")
    private String emailResetPasswordSubject;
    @Value("${email.gmail.login.text}")
    private String emailGmailLoginText;

    @BeforeEach
    void setUp() {
        //given
        userService = Mockito.mock(UserService.class);
        secureTokenService = Mockito.mock(SecureTokenService.class);
        mailSenderService = Mockito.mock(MailSenderService.class);
        mailContentBuilder = Mockito.mock(MailContentBuilderService.class);
        messageSource = Mockito.mock(MessageSource.class);

        passwordRecoveryService = new PasswordRecoveryServiceImpl(userService, secureTokenService, mailSenderService, mailContentBuilder, messageSource);

        ReflectionTestUtils.setField(passwordRecoveryService, "baseURL", baseURL);

        expectedUser = User.builder().id(1).username("TestUser").email("TestUser@mail.com").registrationType(UserRegistrationType.REGISTRATION_FORM).isAccountNonLocked(true).failedLoginAttempt(0).build();
        expectedUserGAuth = User.builder().id(4).username("TestUserGAuth").email("TestUserGAuth@gmail.com").registrationType(UserRegistrationType.GMAIL_AUTHENTICATION).isAccountNonLocked(false).failedLoginAttempt(0).build();

        expectedSecureToken = SecureToken.builder().id(1).token("wMQzFUNrjsXyyht0lF-B").timeStamp(Timestamp.valueOf(LocalDateTime.now())).expireAt(LocalDateTime.now().plusSeconds(tokenValidityInSeconds)).user(expectedUser).build();
    }

    @Test
    void isPasswordRecoveryServiceImplTestReady() {
        Assertions.assertThat(userService).isNotNull().isInstanceOf(UserService.class);
        Assertions.assertThat(mailSenderService).isNotNull().isInstanceOf(MailSenderService.class);
        Assertions.assertThat(mailContentBuilder).isNotNull().isInstanceOf(MailContentBuilderService.class);
        Assertions.assertThat(messageSource).isNotNull().isInstanceOf(MessageSource.class);
        Assertions.assertThat(passwordRecoveryService).isNotNull().isInstanceOf(PasswordRecoveryServiceImpl.class);
        Assertions.assertThat(secureTokenService).isNotNull().isInstanceOf(SecureTokenService.class);
        Assertions.assertThat(expectedUser).isNotNull().isInstanceOf(User.class);
        Assertions.assertThat(expectedSecureToken).isNotNull().isInstanceOf(SecureToken.class);
    }

    @Test
    void forgottenPasswordSuccess() {
        //given
        Mockito.doReturn(expectedUser).when(userService).loadUserByUsername(expectedUser.getUsername());
        Mockito.doReturn(expectedSecureToken).when(secureTokenService).createSecureToken();

        //when
        boolean isSent = passwordRecoveryService.forgottenPassword(expectedUser.getUsername());

        //then
        Mockito.verify(userService, Mockito.times(1)).loadUserByUsername(expectedUser.getUsername());
        Assertions.assertThat(isSent).isTrue();
    }

    @Test
    void forgottenPasswordFailure() {
        //given
        Mockito.doThrow(UsernameNotFoundException.class).when(userService).loadUserByUsername(expectedUser.getUsername());
        Mockito.doReturn(expectedSecureToken).when(secureTokenService).createSecureToken();

        //when
        boolean isSent = passwordRecoveryService.forgottenPassword(expectedUser.getUsername());

        //then
        Mockito.verify(userService, Mockito.times(1)).loadUserByUsername(expectedUser.getUsername());
        Assertions.assertThat(isSent).isFalse();
    }

    @Test
    void updatePasswordSuccess() {
        //given
        Mockito.doReturn(Optional.ofNullable(expectedSecureToken)).when(secureTokenService).findByToken(expectedSecureToken.getToken());
        Mockito.doReturn(expectedUser).when(userService).loadUserByUsername(expectedUser.getUsername());

        //when
        boolean isUpdated = passwordRecoveryService.updatePassword(expectedUser.getPassword(), expectedSecureToken.getToken());

        //then
        Mockito.verify(secureTokenService, Mockito.times(1)).findByToken(expectedSecureToken.getToken());
        Mockito.verify(userService, Mockito.times(1)).loadUserByUsername(expectedUser.getUsername());
        Mockito.verify(secureTokenService, Mockito.times(1)).delete(expectedSecureToken);
        Mockito.verify(userService, Mockito.times(1)).updateUser(expectedUser);
        Assertions.assertThat(isUpdated).isTrue();
    }

    @Test
    void updatePasswordFailure_WithInvalidToken() {
        //given
        Mockito.doReturn(Optional.empty()).when(secureTokenService).findByToken(expectedSecureToken.getToken());

        //when
        boolean isUpdated = passwordRecoveryService.updatePassword(expectedUser.getPassword(), expectedSecureToken.getToken());

        //then
        Mockito.verify(secureTokenService, Mockito.times(1)).findByToken(expectedSecureToken.getToken());
        Mockito.verify(secureTokenService, Mockito.times(0)).delete(expectedSecureToken);
        Mockito.verify(userService, Mockito.times(0)).updateUser(expectedUser);
        Assertions.assertThat(isUpdated).isFalse();
    }

    @Test
    void updatePasswordFailure_WithUsernameNotFoundException() {
        //given
        Mockito.doReturn(Optional.ofNullable(expectedSecureToken)).when(secureTokenService).findByToken(expectedSecureToken.getToken());
        Mockito.doThrow(UsernameNotFoundException.class).when(userService).loadUserByUsername(expectedUser.getUsername());

        //when
        boolean isUpdated = passwordRecoveryService.updatePassword(expectedUser.getPassword(), expectedSecureToken.getToken());

        //then
        Mockito.verify(secureTokenService, Mockito.times(1)).findByToken(expectedSecureToken.getToken());
        Mockito.verify(userService, Mockito.times(1)).loadUserByUsername(expectedUser.getUsername());
        Mockito.verify(secureTokenService, Mockito.times(0)).delete(expectedSecureToken);
        Mockito.verify(userService, Mockito.times(0)).updateUser(expectedUser);
        Assertions.assertThat(isUpdated).isFalse();
    }

    @Test
    void sendResetPasswordEmail_With_REGISTRATION_FORM() {
        //given
        Mockito.doReturn(expectedSecureToken).when(secureTokenService).createSecureToken();

        List<String> strings = new ArrayList<>();
        strings.add(expectedUser.getName());
        strings.add(emailResetPasswordMessageToLink);
        strings.add(UriComponentsBuilder.fromHttpUrl(baseURL).path("/password/change").queryParam("token", expectedSecureToken.getToken()).toUriString());

        //when
        passwordRecoveryService.sendResetPasswordEmail(expectedUser);

        //then
        Mockito.verify(secureTokenService, Mockito.times(1)).update(expectedSecureToken);
        Mockito.verify(mailSenderService, Mockito.times(1))
                .sendEmail(expectedUser.getEmail(), null, mailContentBuilder.build(strings, "email/forgotPassword"));
        Mockito.verify(mailContentBuilder, Mockito.times(1)).build(strings, "email/forgotPassword");
    }

    @Test
    void sendResetPasswordEmail_With_GMAIL_AUTHENTICATION() {
        //given
        Mockito.doReturn(expectedSecureToken).when(secureTokenService).createSecureToken();
        List<String> strings = new ArrayList<>();
        strings.add(expectedUserGAuth.getName());
        strings.add(emailGmailLoginText);
        strings.add(UriComponentsBuilder.fromHttpUrl(baseURL).path("/password/change").queryParam("token", expectedSecureToken.getToken()).toUriString());

        //when
        passwordRecoveryService.sendResetPasswordEmail(expectedUserGAuth);

        //then
        Mockito.verify(secureTokenService, Mockito.times(1)).update(expectedSecureToken);
        Mockito.verify(mailSenderService, Mockito.times(1))
                .sendEmail(expectedUserGAuth.getEmail(), null, mailContentBuilder.build(strings, "email/loginViaGmail"));
        Mockito.verify(mailContentBuilder, Mockito.times(1)).build(strings, "email/loginViaGmail");
    }

    @Test
    void getResetPasswordUrl() {
        //when
        String actualURL = passwordRecoveryService.getResetPasswordUrl(expectedSecureToken.getToken());

        //then
        Assertions.assertThat(actualURL).isEqualTo("http://localhost:8080/password/change?token=wMQzFUNrjsXyyht0lF-B");
    }

    @Test
    void loginDisabledUsernameNotFoundException() {
        //given
        Mockito.doThrow(UsernameNotFoundException.class).when(userService).loadUserByUsername(expectedUser.getUsername());

        //when
        boolean isLocked = passwordRecoveryService.loginDisabled(expectedUser.getUsername());

        //then
        Assertions.assertThat(isLocked).isFalse();
    }

    @Test
    void loginDisabledSuccess() {
        //given
        expectedUser.setAccountNonLocked(false);
        Mockito.doReturn(expectedUser).when(userService).loadUserByUsername(expectedUser.getUsername());
        Mockito.doReturn(expectedSecureToken).when(secureTokenService).createSecureToken();

        //when
        boolean isLocked = passwordRecoveryService.loginDisabled(expectedUser.getUsername());

        //then
        Assertions.assertThat(isLocked).isTrue();
    }

    @Test
    void loginDisabledFailure() {
        //given
        Mockito.doReturn(expectedUser).when(userService).loadUserByUsername(expectedUser.getUsername());

        //when
        boolean isLocked = passwordRecoveryService.loginDisabled(expectedUser.getUsername());

        //then
        Assertions.assertThat(isLocked).isFalse();
    }
}