package com.github.vladyslavbabenko.mycoloroflife.controller.privateAreaController;

import com.github.vladyslavbabenko.mycoloroflife.controller.AbstractControllerIntegrationTest;
import com.github.vladyslavbabenko.mycoloroflife.entity.ActivationCode;
import com.github.vladyslavbabenko.mycoloroflife.entity.CourseTitle;
import com.github.vladyslavbabenko.mycoloroflife.entity.SecureToken;
import com.github.vladyslavbabenko.mycoloroflife.entity.User;
import com.github.vladyslavbabenko.mycoloroflife.enumeration.UserRegistrationType;
import org.fest.assertions.api.Assertions;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockServletContext;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;

import javax.servlet.ServletContext;
import java.util.Locale;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WithMockUser(username = "TestUser", roles = "USER")
@DisplayName("Integration-level testing for PrivateAreaControllerController as User")
@Sql(value = {"/create-test-values.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class PrivateAreaControllerAsUserIntegrationTest extends AbstractControllerIntegrationTest {

    private User testUser, testUserGAuth, testAuthor;
    private CourseTitle testCourseTitle;
    private ActivationCode testActivationCode;
    private SecureToken testSecureToken;

    @BeforeEach
    void setUp() {
        super.setup();

        testUser = User.builder()
                .id(1)
                .name("TestUser")
                .email("TestUser@mail.com")
                .password("123456")
                .passwordConfirm("123456")
                .isEmailConfirmed(false)
                .registrationType(UserRegistrationType.REGISTRATION_FORM)
                .build();

        testAuthor = User.builder()
                .id(3)
                .name("TestAuthor")
                .email("TestAuthor@mail.com")
                .password("123456")
                .passwordConfirm("123456")
                .isEmailConfirmed(false)
                .registrationType(UserRegistrationType.REGISTRATION_FORM)
                .build();

        testUserGAuth = User.builder()
                .id(4)
                .name("TestUserGAuth")
                .email("TestUserGAuth@gmail.com")
                .registrationType(UserRegistrationType.GMAIL_AUTHENTICATION)
                .isEmailConfirmed(true)
                .build();

        testCourseTitle = CourseTitle.builder().id(1).title("Test").description("Test description").build();

        testActivationCode = ActivationCode.builder().id(1).code("Q5sxTc941iokNy8").user(testUser).courseTitle(testCourseTitle).build();

        testSecureToken = SecureToken.builder().id(1).token("wMQzFUNrjsXyyht0lF-B").user(testUser).build();

    }

    @Test
    public void isPrivateAreaControllerIntegrationTestSetUpForTests() {
        ServletContext servletContext = webApplicationContext.getServletContext();

        Assertions.assertThat(servletContext).isNotNull().isInstanceOf(MockServletContext.class);
        Assertions.assertThat(webApplicationContext.getBean("privateAreaController")).isNotNull();
    }

    @Test
    public void GET_PrivateAreaPageAsUser() throws Exception {
        this.mockMvc.perform(get("/me"))
                .andDo(print())
                .andExpect(view().name("userTemplate/privateAreaPage"))
                .andExpect(model().attribute("user", Matchers.any(User.class)))
                .andExpect(status().isOk());
    }

    @Test
    public void GET_ChangePsswordPageAsUser() throws Exception {
        this.mockMvc.perform(get("/me/change-password"))
                .andDo(print())
                .andExpect(view().name("userTemplate/changePasswordPage"))
                .andExpect(model().attribute("user", Matchers.any(User.class)))
                .andExpect(status().isOk());
    }

    @Test
    public void GET_EditPageAsUser() throws Exception {
        this.mockMvc.perform(get("/me/edit"))
                .andDo(print())
                .andExpect(view().name("userTemplate/editPage"))
                .andExpect(model().attribute("user", Matchers.any(User.class)))
                .andExpect(status().isOk());
    }

    @Test
    public void PATCH_ChangePasswordPageAsUser_WithPasswordOutOfBounds() throws Exception {
        SecurityContextImpl securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(new RememberMeAuthenticationToken(
                "TestUser", testUser, AuthorityUtils.createAuthorityList("ROLE_USER")));
        SecurityContextHolder.setContext(securityContext);

        String oldPassword = testUser.getPassword();

        testUser.setPassword("1");
        testUser.setPasswordConfirm("1");

        String errorMessage = "Довжина пароля має бути від 5 до 30 символів";

        this.mockMvc.perform(patch("/me/change-password")
                        .param("id", String.valueOf(testUser.getId()))
                        .param("username", testUser.getUsername())
                        .param("email", testUser.getEmail())
                        .param("oldPassword", oldPassword)
                        .param("password", testUser.getPassword())
                        .param("passwordConfirm", testUser.getPasswordConfirm()))
                .andDo(print())
                .andExpect(view().name("userTemplate/changePasswordPage"))
                .andExpect(model().attribute("passwordOutOfBounds", Matchers.equalTo(errorMessage)))
                .andExpect(content().string(Matchers.containsString(errorMessage)))
                .andExpect(status().isOk());
    }

    @Test
    public void PATCH_ChangePasswordPageAsUser_WithPasswordMismatch() throws Exception {
        SecurityContextImpl securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(new RememberMeAuthenticationToken(
                "TestUser", testUser, AuthorityUtils.createAuthorityList("ROLE_USER")));
        SecurityContextHolder.setContext(securityContext);

        String oldPassword = testUser.getPassword();

        testUser.setPasswordConfirm(testUser.getPassword() + testUser.getPassword());

        String errorMessage = "Паролі не співпадають";

        this.mockMvc.perform(patch("/me/change-password")
                        .param("id", String.valueOf(testUser.getId()))
                        .param("username", testUser.getUsername())
                        .param("email", testUser.getEmail())
                        .param("oldPassword", oldPassword)
                        .param("password", testUser.getPassword())
                        .param("passwordConfirm", testUser.getPasswordConfirm()))
                .andDo(print())
                .andExpect(view().name("userTemplate/changePasswordPage"))
                .andExpect(model().attribute("userPasswordMismatch", Matchers.equalTo(errorMessage)))
                .andExpect(content().string(Matchers.containsString(errorMessage)))
                .andExpect(status().isOk());
    }

    @Test
    public void PATCH_ChangePasswordPageAsUser_WithPasswordZeroLength() throws Exception {
        SecurityContextImpl securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(new RememberMeAuthenticationToken(
                "TestUser", testUser, AuthorityUtils.createAuthorityList("ROLE_USER")));
        SecurityContextHolder.setContext(securityContext);

        String oldPassword = testUser.getPassword();

        testUser.setPassword("");

        this.mockMvc.perform(patch("/me/change-password")
                        .param("id", String.valueOf(testUser.getId()))
                        .param("username", testUser.getUsername())
                        .param("email", testUser.getEmail())
                        .param("oldPassword", oldPassword)
                        .param("password", testUser.getPassword())
                        .param("passwordConfirm", testUser.getPasswordConfirm()))
                .andDo(print())
                .andExpect(view().name("userTemplate/privateAreaPage"))
                .andExpect(status().isOk());
    }

    @Test
    public void PATCH_ChangePasswordPageAsUser_WithOldPasswordMismatch() throws Exception {
        SecurityContextImpl securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(new RememberMeAuthenticationToken(
                "TestUser", testUser, AuthorityUtils.createAuthorityList("ROLE_USER")));
        SecurityContextHolder.setContext(securityContext);

        String oldPassword = testUser.getPassword() + testUser.getPassword();

        String errorMessage = "Невірний пароль";

        this.mockMvc.perform(patch("/me/change-password")
                        .param("id", String.valueOf(testUser.getId()))
                        .param("username", testUser.getUsername())
                        .param("email", testUser.getEmail())
                        .param("oldPassword", oldPassword)
                        .param("password", testUser.getPassword())
                        .param("passwordConfirm", testUser.getPasswordConfirm()))
                .andDo(print())
                .andExpect(view().name("userTemplate/changePasswordPage"))
                .andExpect(model().attribute("invalidOldPassword", Matchers.equalTo(errorMessage)))
                .andExpect(content().string(Matchers.containsString(errorMessage)))
                .andExpect(status().isOk());
    }


    @Test
    public void PATCH_ChangePasswordPageAsUser_Failure_WithInvalidUsername() throws Exception {
        String randomEmail = testUser.getName() + testUser.getId() + "@mail.com";
        testUser.setEmail(randomEmail);
        testUser.setId(Integer.MAX_VALUE);

        SecurityContextImpl securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(new RememberMeAuthenticationToken(
                "TestUser", testUser, AuthorityUtils.createAuthorityList("ROLE_USER")));
        SecurityContextHolder.setContext(securityContext);

        String oldPassword = testUser.getPassword();

        String errorMessage = "Користувач не знайдений";

        this.mockMvc.perform(patch("/me/change-password")
                        .param("id", String.valueOf(testUser.getId()))
                        .param("username", testUser.getUsername())
                        .param("email", testUser.getEmail())
                        .param("oldPassword", oldPassword)
                        .param("password", testUser.getPassword())
                        .param("passwordConfirm", testUser.getPasswordConfirm()))
                .andDo(print())
                .andExpect(view().name("userTemplate/changePasswordPage"))
                .andExpect(model().attribute("userNotFound", Matchers.equalTo(errorMessage)))
                .andExpect(content().string(Matchers.containsString(errorMessage)))
                .andExpect(status().isOk());
    }

    @Test
    public void PATCH_ChangePasswordPageAsUser_WithChangePasswordSuccess() throws Exception {
        SecurityContextImpl securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(new RememberMeAuthenticationToken(
                "TestUser", testUser, AuthorityUtils.createAuthorityList("ROLE_USER")));
        SecurityContextHolder.setContext(securityContext);

        String oldPassword = testUser.getPassword();

        this.mockMvc.perform(patch("/me/change-password")
                        .param("id", String.valueOf(testUser.getId()))
                        .param("username", testUser.getUsername())
                        .param("email", testUser.getEmail())
                        .param("oldPassword", oldPassword)
                        .param("password", testUser.getPassword())
                        .param("passwordConfirm", testUser.getPasswordConfirm()))
                .andDo(print())
                .andExpect(view().name("userTemplate/privateAreaPage"))
                .andExpect(status().isOk());
    }

    @Test
    public void PATCH_EditPageAsUser_WithError() throws Exception {
        SecurityContextImpl securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(new RememberMeAuthenticationToken(
                "TestUser", testUser, AuthorityUtils.createAuthorityList("ROLE_USER")));
        SecurityContextHolder.setContext(securityContext);

        String errorMessage = "Пошта має бути валідною";

        testUser.setEmail("TestUsermailcom");

        this.mockMvc.perform(patch("/me/edit")
                        .param("id", String.valueOf(testUser.getId()))
                        .param("username", testUser.getUsername())
                        .param("email", testUser.getEmail())
                        .param("password", testUser.getPassword())
                        .param("passwordConfirm", testUser.getPasswordConfirm()))
                .andDo(print())
                .andExpect(content().string(Matchers.containsString(errorMessage)))
                .andExpect(view().name("userTemplate/editPage"))
                .andExpect(status().isOk());
    }

    @Test
    public void PATCH_EditPageAsUser_WithUpdateUserSuccess() throws Exception {
        SecurityContextImpl securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(new RememberMeAuthenticationToken(
                "TestUser", testUser, AuthorityUtils.createAuthorityList("ROLE_USER")));
        SecurityContextHolder.setContext(securityContext);

        this.mockMvc.perform(patch("/me/edit")
                        .param("id", String.valueOf(testUser.getId()))
                        .param("username", testUser.getUsername())
                        .param("email", testUser.getEmail())
                        .param("password", testUser.getPassword())
                        .param("passwordConfirm", testUser.getPasswordConfirm()))
                .andDo(print())
                .andExpect(redirectedUrl("/me"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    public void PUT_ActivateCodeAsUser_Failure_WithEmptyActivationCode() throws Exception {
        this.mockMvc.perform(put("/me/activate-code")
                        .param("activationCode", ""))
                .andDo(print())
                .andExpect(view().name("userTemplate/privateAreaPage"))
                .andExpect(status().isOk());
    }

    @Test
    public void PUT_ActivateCodeAsUser_Failure_WithUserEmailNotConfirmed() throws Exception {
        SecurityContextImpl securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(new RememberMeAuthenticationToken(
                "TestUser", testUser, AuthorityUtils.createAuthorityList("ROLE_USER")));
        SecurityContextHolder.setContext(securityContext);

        String errorMessage = "Спочатку підтвердьте свою електронну адресу";

        this.mockMvc.perform(put("/me/activate-code")
                        .param("activationCode", testActivationCode.getCode()))
                .andDo(print())
                .andExpect(view().name("userTemplate/privateAreaPage"))
                .andExpect(model().attribute("user", Matchers.any(User.class)))
                .andExpect(model().attribute("userActivationCodeEmailConfirm", Matchers.equalTo(errorMessage)))
                .andExpect(content().string(Matchers.containsString(errorMessage)))
                .andExpect(status().isOk());
    }

    @Test
    public void PUT_ActivateCodeAsUser_Failure_WithActivationCodeDoesNotExist() throws Exception {
        SecurityContextImpl securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(new RememberMeAuthenticationToken(
                "TestUser", testUserGAuth, AuthorityUtils.createAuthorityList("ROLE_USER")));
        SecurityContextHolder.setContext(securityContext);

        String errorMessage = "Недійсний код активації";

        this.mockMvc.perform(put("/me/activate-code")
                        .param("activationCode", testActivationCode.getCode().toUpperCase(Locale.ROOT)))
                .andDo(print())
                .andExpect(view().name("userTemplate/privateAreaPage"))
                .andExpect(model().attribute("user", Matchers.any(User.class)))
                .andExpect(model().attribute("userInvalidActivationCode", Matchers.equalTo(errorMessage)))
                .andExpect(content().string(Matchers.containsString(errorMessage)))
                .andExpect(status().isOk());
    }

    @Test
    public void PUT_ActivateCodeAsUser_Success() throws Exception {
        SecurityContextImpl securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(new RememberMeAuthenticationToken(
                "TestUser", testUserGAuth, AuthorityUtils.createAuthorityList("ROLE_USER")));
        SecurityContextHolder.setContext(securityContext);

        this.mockMvc.perform(put("/me/activate-code")
                        .param("activationCode", testActivationCode.getCode()))
                .andDo(print())
                .andExpect(view().name("userTemplate/privateAreaPage"))
                .andExpect(status().isOk());
    }

    /////TEMP\\\\\\
    @Test
    @Sql(value = {"/forSelfGenerateCodeDeleteLater/clear-activation-codes.sql", "/forSelfGenerateCodeDeleteLater/clear-course-title.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void POST_SelfGenerateCodeAsUser_Failure_WithCourseTitleDoesNotExist() throws Exception {
        this.mockMvc.perform(post("/me/generate/activation-code"))
                .andDo(print())
                .andExpect(view().name("userTemplate/privateAreaPage"))
                .andExpect(model().attribute("user", Matchers.any(User.class)))
                .andExpect(status().isOk());
    }

    @Test
    @Sql(value = {"/forSelfGenerateCodeDeleteLater/clear-course-owner-role.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void POST_SelfGenerateCodeAsUser_Failure_WithRoleForCourseTitleDoesNotExist() throws Exception {
        this.mockMvc.perform(post("/me/generate/activation-code"))
                .andDo(print())
                .andExpect(view().name("userTemplate/privateAreaPage"))
                .andExpect(model().attribute("user", Matchers.any(User.class)))
                .andExpect(status().isOk());
    }

    @Test
    @Sql(value = {"/forSelfGenerateCodeDeleteLater/create-activation-code.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void POST_SelfGenerateCodeAsUser_Failure_WithCodeAlreadyGenerated() throws Exception {
        SecurityContextImpl securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(new RememberMeAuthenticationToken(
                "testUserGAuth", testUserGAuth, AuthorityUtils.createAuthorityList("ROLE_USER")));
        SecurityContextHolder.setContext(securityContext);

        String errorMessage = "Ви вже згенерували тестовий код активації";

        this.mockMvc.perform(post("/me/generate/activation-code"))
                .andDo(print())
                .andExpect(view().name("userTemplate/privateAreaPage"))
                .andExpect(model().attribute("user", Matchers.any(User.class)))
                .andExpect(model().attribute("userActivationCodeAlreadyGenerated", Matchers.equalTo(errorMessage)))
                .andExpect(content().string(Matchers.containsString(errorMessage)))
                .andExpect(status().isOk());
    }

    @Test
    public void POST_SelfGenerateCodeAsUser_Failure_WithEmailNotConfirmed() throws Exception {
        SecurityContextImpl securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(new RememberMeAuthenticationToken(
                "TestUser", testUser, AuthorityUtils.createAuthorityList("ROLE_USER")));
        SecurityContextHolder.setContext(securityContext);

        String message = "Спочатку підтвердьте свою електронну адресу";

        this.mockMvc.perform(post("/me/generate/activation-code"))
                .andDo(print())
                .andExpect(view().name("userTemplate/privateAreaPage"))
                .andExpect(model().attribute("user", Matchers.any(User.class)))
                .andExpect(model().attribute("userActivationCodeEmailConfirm", Matchers.equalTo(message)))
                .andExpect(status().isOk());
    }

    @Test
    @Sql(value = {"/forSelfGenerateCodeDeleteLater/clear-activation-codes.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void POST_SelfGenerateCodeAsUser_Success() throws Exception {
        SecurityContextImpl securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(new RememberMeAuthenticationToken(
                "testUserGAuth", testUserGAuth, AuthorityUtils.createAuthorityList("ROLE_USER")));
        SecurityContextHolder.setContext(securityContext);

        String message = "Код активації було надіслано на Вашу пошту";

        this.mockMvc.perform(post("/me/generate/activation-code"))
                .andDo(print())
                .andExpect(view().name("userTemplate/privateAreaPage"))
                .andExpect(model().attribute("user", Matchers.any(User.class)))
                .andExpect(model().attribute("userActivationCodeEmailSent", Matchers.equalTo(message)))
                .andExpect(status().isOk());
    }

    @Test
    public void POST_EmailRequest_FailureTokenAlreadyExistsByUserAndPurpose() throws Exception {
        SecurityContextImpl securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(new RememberMeAuthenticationToken(
                "testUser", testUser, AuthorityUtils.createAuthorityList("ROLE_USER")));
        SecurityContextHolder.setContext(securityContext);

        String message = "На вашу електронну адресу надіслано лист із посиланням для підтвердження";

        this.mockMvc.perform(post("/me/email-request"))
                .andDo(print())
                .andExpect(view().name("userTemplate/privateAreaPage"))
                .andExpect(model().attribute("user", Matchers.any(User.class)))
                .andExpect(model().attribute("message", Matchers.equalTo(message)))
                .andExpect(status().isOk());
    }

    @Test
    @Sql(value = {"/clear-secure-tokens.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void POST_EmailRequest_Success() throws Exception {
        SecurityContextImpl securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(new RememberMeAuthenticationToken(
                "testAuthor", testAuthor, AuthorityUtils.createAuthorityList("ROLE_USER")));
        SecurityContextHolder.setContext(securityContext);

        String message = "На вашу електронну адресу надіслано лист із посиланням для підтвердження";

        this.mockMvc.perform(post("/me/email-request"))
                .andDo(print())
                .andExpect(view().name("userTemplate/privateAreaPage"))
                .andExpect(model().attribute("user", Matchers.any(User.class)))
                .andExpect(model().attribute("message", Matchers.equalTo(message)))
                .andExpect(status().isOk());
    }

    @Test
    public void GET_EmailConfirm_Failure_WithNoToken() throws Exception {
        String errorMessage = "Відсутній токен";

        this.mockMvc.perform(get("/me/email-confirm"))
                .andDo(print())
                .andExpect(view().name("generalTemplate/emailConfirmPage"))
                .andExpect(model().attribute("tokenError", Matchers.equalTo(errorMessage)))
                .andExpect(status().isOk());
    }

    @Test
    public void GET_EmailConfirm_Failure_WithInvalidError() throws Exception {
        String errorMessage = "Неправильний або застарілий токен";

        this.mockMvc.perform(get("/me/email-confirm")
                        .param("token", testUser.getUsername()))
                .andDo(print())
                .andExpect(view().name("generalTemplate/emailConfirmPage"))
                .andExpect(model().attribute("tokenError", Matchers.equalTo(errorMessage)))
                .andExpect(status().isOk());
    }

    @Test
    public void GET_EmailConfirm_Success() throws Exception {
        this.mockMvc.perform(get("/me/email-confirm")
                        .param("token", testSecureToken.getToken()))
                .andDo(print())
                .andExpect(view().name("generalTemplate/emailConfirmPage"))
                .andExpect(model().attribute("token", Matchers.equalTo(testSecureToken.getToken())))
                .andExpect(model().attribute("email", Matchers.equalTo(testSecureToken.getUser().getEmail())))
                .andExpect(status().isOk());
    }

    @Test
    public void POST_EmailConfirm_Failure_WithInvalidToken() throws Exception {

        String message = "Неправильний або застарілий токен";

        this.mockMvc.perform(post("/me/email-confirm"))
                .andDo(print())
                .andExpect(view().name("userTemplate/privateAreaPage"))
                .andExpect(model().attribute("user", Matchers.any(User.class)))
                .andExpect(model().attribute("message", Matchers.equalTo(message)))
                .andExpect(status().isOk());
    }

    @Test
    public void POST_EmailConfirm_Success() throws Exception {

        String message = "Вашу електронну адресу успішно підтверджено";

        this.mockMvc.perform(post("/me/email-confirm")
                        .param("token", testSecureToken.getToken()))
                .andDo(print())
                .andExpect(view().name("userTemplate/privateAreaPage"))
                .andExpect(model().attribute("user", Matchers.any(User.class)))
                .andExpect(model().attribute("message", Matchers.equalTo(message)))
                .andExpect(status().isOk());
    }
}