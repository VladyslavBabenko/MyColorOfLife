package com.github.vladyslavbabenko.mycoloroflife.service.implementation;

import com.github.vladyslavbabenko.mycoloroflife.entity.SecureToken;
import com.github.vladyslavbabenko.mycoloroflife.entity.User;
import com.github.vladyslavbabenko.mycoloroflife.enumeration.Purpose;
import com.github.vladyslavbabenko.mycoloroflife.service.MailContentBuilderService;
import com.github.vladyslavbabenko.mycoloroflife.service.MailSenderService;
import com.github.vladyslavbabenko.mycoloroflife.service.SecureTokenService;
import com.github.vladyslavbabenko.mycoloroflife.service.UserService;
import com.github.vladyslavbabenko.mycoloroflife.util.MessageSourceUtil;
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
@DisplayName("Unit-level testing for EmailConfirmationServiceImpl")
class EmailConfirmationServiceImplTest {

    private UserService userService;
    private SecureTokenService secureTokenService;
    private MailSenderService mailSenderService;
    private MailContentBuilderService mailContentBuilder;
    private MessageSourceUtil messageSource;
    private EmailConfirmationServiceImpl emailConfirmationService;

    private SecureToken expectedSecureToken;
    private User expectedUser;

    @Value("${secure.token.validity}")
    private int tokenValidityInSeconds;

    @Value("${site.base.url.http}")
    private String baseURL;

    @Value("${email.confirm.text}")
    private String emailConfirmText;

    @BeforeEach
    void setUp() {
        //given
        userService = Mockito.mock(UserService.class);
        secureTokenService = Mockito.mock(SecureTokenService.class);
        mailSenderService = Mockito.mock(MailSenderService.class);
        mailContentBuilder = Mockito.mock(MailContentBuilderService.class);
        messageSource = Mockito.mock(MessageSourceUtil.class);

        emailConfirmationService = new EmailConfirmationServiceImpl(userService, secureTokenService, mailSenderService, mailContentBuilder, messageSource);

        expectedUser = User.builder()
                .id(1)
                .name("TestUser")
                .email("TestUser@mail.com")
                .build();

        ReflectionTestUtils.setField(emailConfirmationService, "baseURL", baseURL);

        expectedSecureToken = SecureToken.builder().id(1).token("wMQzFUNrjsXyyht0lF-B").timeStamp(Timestamp.valueOf(LocalDateTime.now())).expireAt(LocalDateTime.now().plusSeconds(tokenValidityInSeconds)).user(expectedUser).purpose(Purpose.EMAIL_CONFIRM).build();
    }

    @Test
    void isEmailConfirmationServiceImplTestReady() {
        Assertions.assertThat(userService).isNotNull().isInstanceOf(UserService.class);
        Assertions.assertThat(mailSenderService).isNotNull().isInstanceOf(MailSenderService.class);
        Assertions.assertThat(mailContentBuilder).isNotNull().isInstanceOf(MailContentBuilderService.class);
        Assertions.assertThat(messageSource).isNotNull().isInstanceOf(MessageSourceUtil.class);
        Assertions.assertThat(emailConfirmationService).isNotNull().isInstanceOf(EmailConfirmationServiceImpl.class);
        Assertions.assertThat(secureTokenService).isNotNull().isInstanceOf(SecureTokenService.class);
        Assertions.assertThat(expectedUser).isNotNull().isInstanceOf(User.class);
        Assertions.assertThat(expectedSecureToken).isNotNull().isInstanceOf(SecureToken.class);
    }

    @Test
    void confirmEmail_Failure_UsernameNotFound() {
        //given
        Mockito.doThrow(UsernameNotFoundException.class).when(userService).confirmEmail(expectedSecureToken.getUser().getUsername());

        //when
        boolean confirmed = emailConfirmationService.confirmEmail(expectedSecureToken.getToken());

        //then
        Mockito.verify(secureTokenService, Mockito.times(1)).findByToken(expectedSecureToken.getToken());
        Mockito.verify(userService, Mockito.times(0)).confirmEmail(expectedSecureToken.getUser().getUsername());
        Mockito.verify(secureTokenService, Mockito.times(0)).delete(expectedSecureToken);
        Assertions.assertThat(confirmed).isFalse();
    }

    @Test
    void confirmEmail_Failure_InvalidToken() {
        //when
        boolean confirmed = emailConfirmationService.confirmEmail(expectedSecureToken.getToken());

        //then
        Mockito.verify(secureTokenService, Mockito.times(1)).findByToken(expectedSecureToken.getToken());
        Mockito.verify(userService, Mockito.times(0)).confirmEmail(expectedSecureToken.getUser().getUsername());
        Mockito.verify(secureTokenService, Mockito.times(0)).delete(expectedSecureToken);
        Assertions.assertThat(confirmed).isFalse();
    }

    @Test
    void confirmEmail_Success() {
        //given
        Mockito.doReturn(Optional.ofNullable(expectedSecureToken)).when(secureTokenService).findByToken(expectedSecureToken.getToken());

        //when
        boolean confirmed = emailConfirmationService.confirmEmail(expectedSecureToken.getToken());

        //then
        Mockito.verify(secureTokenService, Mockito.times(1)).findByToken(expectedSecureToken.getToken());
        Mockito.verify(userService, Mockito.times(1)).confirmEmail(expectedSecureToken.getUser().getUsername());
        Mockito.verify(secureTokenService, Mockito.times(1)).delete(expectedSecureToken);
        Assertions.assertThat(confirmed).isTrue();
    }

    @Test
    void sendConfirmationEmail() {
        //given
        Mockito.doReturn(expectedSecureToken).when(secureTokenService).createSecureToken();

        List<String> strings = new ArrayList<>();
        strings.add(expectedUser.getName());
        strings.add(emailConfirmText);
        strings.add(UriComponentsBuilder.fromHttpUrl(baseURL).path("/me/email-confirm").queryParam("token", expectedSecureToken.getToken()).toUriString());

        //when
        emailConfirmationService.sendConfirmationEmail(expectedUser);

        //then
        Mockito.verify(secureTokenService, Mockito.times(1)).update(expectedSecureToken);
        Mockito.verify(mailSenderService, Mockito.times(1))
                .sendEmail(expectedUser.getEmail(), null, mailContentBuilder.build(strings, "emailTemplate/emailConfirm"));
        Mockito.verify(mailContentBuilder, Mockito.times(1)).build(strings, "emailTemplate/emailConfirm");
    }

    @Test
    void getConfirmationEmailUrl() {
        //when
        String actualURL = emailConfirmationService.getConfirmationEmailUrl(expectedSecureToken.getToken());

        //then
        Assertions.assertThat(actualURL).isEqualTo("http://localhost:8080/me/email-confirm?token=wMQzFUNrjsXyyht0lF-B");
    }
}