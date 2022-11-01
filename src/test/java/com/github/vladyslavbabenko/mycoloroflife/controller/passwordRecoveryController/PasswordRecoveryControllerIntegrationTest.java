package com.github.vladyslavbabenko.mycoloroflife.controller.passwordRecoveryController;

import com.github.vladyslavbabenko.mycoloroflife.controller.AbstractControllerIntegrationTest;
import com.github.vladyslavbabenko.mycoloroflife.entity.SecureToken;
import com.github.vladyslavbabenko.mycoloroflife.entity.User;
import com.github.vladyslavbabenko.mycoloroflife.entity.dto.ResetPasswordData;
import com.github.vladyslavbabenko.mycoloroflife.enumeration.UserRegistrationType;
import org.fest.assertions.api.Assertions;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockServletContext;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.context.jdbc.Sql;

import javax.servlet.ServletContext;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WithAnonymousUser
@DisplayName("Integration-level testing for PasswordRecoveryController as User")
@Sql(value = {"/create-test-values.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class PasswordRecoveryControllerIntegrationTest extends AbstractControllerIntegrationTest {

    private User testUser;
    private SecureToken secureToken;
    private ResetPasswordData resetPasswordData;

    @Value("${secure.token.validity}")
    private int tokenValidityInSeconds;

    @BeforeEach
    void setUp() {
        super.setup();
        testUser = User.builder()
                .id(1)
                .name("TestUser")
                .email("TestUser@mail.com")
                .password("123456")
                .passwordConfirm("123456")
                .registrationType(UserRegistrationType.REGISTRATION_FORM)
                .build();

        secureToken = SecureToken.builder()
                .id(1)
                .token("wMQzFUNrjsXyyht0lF-B")
                .timeStamp(Timestamp.valueOf(LocalDateTime.now()))
                .expireAt(LocalDateTime.now().plusSeconds(tokenValidityInSeconds))
                .user(testUser).build();

        resetPasswordData = ResetPasswordData.builder()
                .token(secureToken.getToken())
                .password(testUser.getPassword())
                .passwordConfirm(testUser.getPasswordConfirm())
                .build();
    }

    @Test
    public void isPasswordRecoveryControllerIntegrationTestSetUpForTests() {
        ServletContext servletContext = webApplicationContext.getServletContext();

        Assertions.assertThat(servletContext).isNotNull().isInstanceOf(MockServletContext.class);
        Assertions.assertThat(webApplicationContext.getBean("passwordRecoveryController")).isNotNull();
    }

    @Test
    void GET_resetPasswordAsAnonymousUser() throws Exception {
        this.mockMvc.perform(get("/password/request"))
                .andDo(print())
                .andExpect(view().name("generalTemplate/requestPasswordChange"))
                .andExpect(status().isOk());
    }

    @Test
    @Sql(value = {"/clear-secure-tokens.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void PATCH_resetPasswordAsAnonymousUser() throws Exception {
        String message = "Якщо на цю пошту було зареєстровано обліковий запис, туди буде надіслано посилання для зміни паролю";

        this.mockMvc.perform(patch("/password/request")
                        .param("email", testUser.getEmail()))
                .andDo(print())
                .andExpect(view().name("generalTemplate/loginPage"))
                .andExpect(model().attribute("message", Matchers.equalTo(message)))
                .andExpect(content().string(Matchers.containsString(message)))
                .andExpect(status().isOk());
    }

    @Test
    void GET_changePasswordAsAnonymousUser_WithNoToken() throws Exception {
        String errorMessage = "Відсутній токен";

        this.mockMvc.perform(get("/password/change")
                        .param("token", ""))
                .andDo(print())
                .andExpect(model().attribute("tokenError", Matchers.equalTo(errorMessage)))
                .andExpect(content().string(Matchers.containsString(errorMessage)))
                .andExpect(view().name("generalTemplate/loginPage"))
                .andExpect(status().isOk());
    }

    @Test
    void GET_changePasswordAsAnonymousUser() throws Exception {
        this.mockMvc.perform(get("/password/change")
                        .param("token", secureToken.getToken()))
                .andDo(print())
                .andExpect(model().attribute("resetPasswordData", Matchers.any(ResetPasswordData.class)))
                .andExpect(view().name("generalTemplate/changePassword"))
                .andExpect(status().isOk());
    }

    @Test
    void POST_changePasswordAsAnonymousUser_WithPasswordMismatch() throws Exception {
        resetPasswordData.setPasswordConfirm(resetPasswordData.getPasswordConfirm().repeat(2));
        String errorMessage = "Паролі не співпадають";

        this.mockMvc.perform(post("/password/change")
                        .param("token", resetPasswordData.getToken())
                        .param("password", resetPasswordData.getPassword())
                        .param("passwordConfirm", resetPasswordData.getPasswordConfirm()))
                .andDo(print())
                .andExpect(view().name("generalTemplate/changePassword"))
                .andExpect(model().attribute("passwordMismatchError", Matchers.equalTo(errorMessage)))
                .andExpect(content().string(Matchers.containsString(errorMessage)))
                .andExpect(status().isOk());
    }

    @Test
    void POST_changePasswordAsAnonymousUser_WithTokenError() throws Exception {
        resetPasswordData.setToken(resetPasswordData.getToken().repeat(2));
        String errorMessage = "Неправильний або застарілий токен";

        this.mockMvc.perform(post("/password/change")
                        .param("token", resetPasswordData.getToken())
                        .param("password", resetPasswordData.getPassword())
                        .param("passwordConfirm", resetPasswordData.getPasswordConfirm()))
                .andDo(print())
                .andExpect(view().name("generalTemplate/loginPage"))
                .andExpect(model().attribute("tokenError", Matchers.equalTo(errorMessage)))
                .andExpect(content().string(Matchers.containsString(errorMessage)))
                .andExpect(status().isOk());
    }

    @Test
    void POST_changePasswordAsAnonymousUser_WithSuccess() throws Exception {
        String message = "Пароль успішно змінений";

        this.mockMvc.perform(post("/password/change")
                        .param("token", resetPasswordData.getToken())
                        .param("password", resetPasswordData.getPassword())
                        .param("passwordConfirm", resetPasswordData.getPasswordConfirm()))
                .andDo(print())
                .andExpect(view().name("generalTemplate/loginPage"))
                .andExpect(model().attribute("message", Matchers.equalTo(message)))
                .andExpect(content().string(Matchers.containsString(message)))
                .andExpect(status().isOk());
    }
}