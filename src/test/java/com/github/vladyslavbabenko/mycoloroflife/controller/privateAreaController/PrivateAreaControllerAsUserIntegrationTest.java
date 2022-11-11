package com.github.vladyslavbabenko.mycoloroflife.controller.privateAreaController;

import com.github.vladyslavbabenko.mycoloroflife.AbstractTest.AbstractControllerIntegrationTest;
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
import org.springframework.beans.factory.annotation.Value;
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


    //Templates
    @Value("${template.user.private-area}")
    String templateUserPrivateArea;

    @Value("${template.user.edit.password}")
    String templateUserEditPassword;

    @Value("${template.user.edit.details}")
    String templateUserEditDetails;

    @Value("${template.general.email.confirm}")
    String templateGeneralEmailConfirm;

    //Messages
    @Value("${user.password.length}")
    String userPasswordLength;

    @Value("${user.password.mismatch}")
    String userPasswordMismatch;

    @Value("${user.password.invalid}")
    String userPasswordInvalid;

    @Value("${user.not.found}")
    String userNotFound;

    @Value("${validation.user.email.not.valid}")
    String validationUserEmailNotValid;

    @Value("${user.activation-code.email.confirm}")
    String userActivationCodeEmailConfirm;

    @Value("${user.activation-code.already.generated}")
    String userActivationCodeAlreadyGenerated;

    @Value("${user.invalid.activation-code}")
    String userInvalidActivationCode;

    @Value("${user.activation-code.email.sent}")
    String userActivationCodeEmailSent;

    @Value("${user.email.confirm.sent}")
    String userEmailConfirmSent;

    @Value("${empty.token}")
    String emptyToken;

    @Value("${invalid.token}")
    String invalidToken;

    @Value("${user.email.confirm.success}")
    String userEmailConfirmSuccess;

    @Value("${role.user}")
    String roleUser;

    @BeforeEach
    void setUp() {
        super.setup();

        testUser = User.builder()
                .id(1)
                .name("TestUser")
                .email("testuser@mail.com")
                .password("123456")
                .passwordConfirm("123456")
                .isEmailConfirmed(false)
                .registrationType(UserRegistrationType.REGISTRATION_FORM)
                .build();

        testAuthor = User.builder()
                .id(3)
                .name("TestAuthor")
                .email("testauthor@mail.com")
                .password("123456")
                .passwordConfirm("123456")
                .isEmailConfirmed(false)
                .registrationType(UserRegistrationType.REGISTRATION_FORM)
                .build();

        testUserGAuth = User.builder()
                .id(4)
                .name("TestUserGAuth")
                .email("testusergauth@gmail.com")
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
                .andExpect(view().name(templateUserPrivateArea))
                .andExpect(model().attribute("user", Matchers.any(User.class)))
                .andExpect(status().isOk());
    }

    @Test
    public void GET_ChangePsswordPageAsUser() throws Exception {
        this.mockMvc.perform(get("/me/change-password"))
                .andDo(print())
                .andExpect(view().name(templateUserEditPassword))
                .andExpect(model().attribute("user", Matchers.any(User.class)))
                .andExpect(status().isOk());
    }

    @Test
    public void GET_EditPageAsUser() throws Exception {
        this.mockMvc.perform(get("/me/edit"))
                .andDo(print())
                .andExpect(view().name(templateUserEditDetails))
                .andExpect(model().attribute("user", Matchers.any(User.class)))
                .andExpect(status().isOk());
    }

    @Test
    public void PATCH_ChangePasswordPageAsUser_WithPasswordOutOfBounds() throws Exception {
        SecurityContextImpl securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(new RememberMeAuthenticationToken(
                "TestUser", testUser, AuthorityUtils.createAuthorityList(roleUser)));
        SecurityContextHolder.setContext(securityContext);

        String oldPassword = testUser.getPassword();

        testUser.setPassword("1");
        testUser.setPasswordConfirm("1");

        String errorMessage = userPasswordLength;

        this.mockMvc.perform(patch("/me/change-password")
                        .param("id", String.valueOf(testUser.getId()))
                        .param("username", testUser.getUsername())
                        .param("email", testUser.getEmail())
                        .param("oldPassword", oldPassword)
                        .param("password", testUser.getPassword())
                        .param("passwordConfirm", testUser.getPasswordConfirm()))
                .andDo(print())
                .andExpect(view().name(templateUserEditPassword))
                .andExpect(model().attribute("passwordOutOfBounds", Matchers.equalTo(errorMessage)))
                .andExpect(content().string(Matchers.containsString(errorMessage)))
                .andExpect(status().isOk());
    }

    @Test
    public void PATCH_ChangePasswordPageAsUser_WithPasswordMismatch() throws Exception {
        SecurityContextImpl securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(new RememberMeAuthenticationToken(
                "TestUser", testUser, AuthorityUtils.createAuthorityList(roleUser)));
        SecurityContextHolder.setContext(securityContext);

        String oldPassword = testUser.getPassword();

        testUser.setPasswordConfirm(testUser.getPassword() + testUser.getPassword());

        String errorMessage = userPasswordMismatch;

        this.mockMvc.perform(patch("/me/change-password")
                        .param("id", String.valueOf(testUser.getId()))
                        .param("username", testUser.getUsername())
                        .param("email", testUser.getEmail())
                        .param("oldPassword", oldPassword)
                        .param("password", testUser.getPassword())
                        .param("passwordConfirm", testUser.getPasswordConfirm()))
                .andDo(print())
                .andExpect(view().name(templateUserEditPassword))
                .andExpect(model().attribute("userPasswordMismatch", Matchers.equalTo(errorMessage)))
                .andExpect(content().string(Matchers.containsString(errorMessage)))
                .andExpect(status().isOk());
    }

    @Test
    public void PATCH_ChangePasswordPageAsUser_WithPasswordZeroLength() throws Exception {
        SecurityContextImpl securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(new RememberMeAuthenticationToken(
                "TestUser", testUser, AuthorityUtils.createAuthorityList(roleUser)));
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
                .andExpect(view().name(templateUserPrivateArea))
                .andExpect(status().isOk());
    }

    @Test
    public void PATCH_ChangePasswordPageAsUser_WithOldPasswordMismatch() throws Exception {
        SecurityContextImpl securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(new RememberMeAuthenticationToken(
                "TestUser", testUser, AuthorityUtils.createAuthorityList(roleUser)));
        SecurityContextHolder.setContext(securityContext);

        String oldPassword = testUser.getPassword() + testUser.getPassword();

        String errorMessage = userPasswordInvalid;

        this.mockMvc.perform(patch("/me/change-password")
                        .param("id", String.valueOf(testUser.getId()))
                        .param("username", testUser.getUsername())
                        .param("email", testUser.getEmail())
                        .param("oldPassword", oldPassword)
                        .param("password", testUser.getPassword())
                        .param("passwordConfirm", testUser.getPasswordConfirm()))
                .andDo(print())
                .andExpect(view().name(templateUserEditPassword))
                .andExpect(model().attribute("invalidOldPassword", Matchers.equalTo(errorMessage)))
                .andExpect(content().string(Matchers.containsString(errorMessage)))
                .andExpect(status().isOk());
    }


    @Test
    public void PATCH_ChangePasswordPageAsUser_Failure_WithInvalidUsername() throws Exception {
        String randomEmail = testUser.getId() + testUser.getName() + testUser.getId() + "@mail.com";
        testUser.setEmail(randomEmail);
        testUser.setId(Integer.MAX_VALUE);

        SecurityContextImpl securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(new RememberMeAuthenticationToken(
                "TestUser", testUser, AuthorityUtils.createAuthorityList(roleUser)));
        SecurityContextHolder.setContext(securityContext);

        String oldPassword = testUser.getPassword();

        String errorMessage = userNotFound;

        this.mockMvc.perform(patch("/me/change-password")
                        .param("id", String.valueOf(testUser.getId()))
                        .param("username", testUser.getUsername())
                        .param("email", testUser.getEmail())
                        .param("oldPassword", oldPassword)
                        .param("password", testUser.getPassword())
                        .param("passwordConfirm", testUser.getPasswordConfirm()))
                .andDo(print())
                .andExpect(view().name(templateUserEditPassword))
                .andExpect(model().attribute("userNotFound", Matchers.equalTo(errorMessage)))
                .andExpect(content().string(Matchers.containsString(errorMessage)))
                .andExpect(status().isOk());
    }

    @Test
    public void PATCH_ChangePasswordPageAsUser_WithChangePasswordSuccess() throws Exception {
        SecurityContextImpl securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(new RememberMeAuthenticationToken(
                "TestUser", testUser, AuthorityUtils.createAuthorityList(roleUser)));
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
                .andExpect(view().name(templateUserPrivateArea))
                .andExpect(status().isOk());
    }

    @Test
    public void PATCH_EditPageAsUser_WithError() throws Exception {
        SecurityContextImpl securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(new RememberMeAuthenticationToken(
                "TestUser", testUser, AuthorityUtils.createAuthorityList(roleUser)));
        SecurityContextHolder.setContext(securityContext);

        String errorMessage = validationUserEmailNotValid;

        testUser.setEmail("TestUsermailcom");

        this.mockMvc.perform(patch("/me/edit")
                        .param("id", String.valueOf(testUser.getId()))
                        .param("username", testUser.getUsername())
                        .param("email", testUser.getEmail())
                        .param("password", testUser.getPassword())
                        .param("passwordConfirm", testUser.getPasswordConfirm()))
                .andDo(print())
                .andExpect(content().string(Matchers.containsString(errorMessage)))
                .andExpect(view().name(templateUserEditDetails))
                .andExpect(status().isOk());
    }

    @Test
    public void PATCH_EditPageAsUser_WithUpdateUserSuccess() throws Exception {
        SecurityContextImpl securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(new RememberMeAuthenticationToken(
                "TestUser", testUser, AuthorityUtils.createAuthorityList(roleUser)));
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
                .andExpect(view().name(templateUserPrivateArea))
                .andExpect(status().isOk());
    }

    @Test
    public void PUT_ActivateCodeAsUser_Failure_WithUserEmailNotConfirmed() throws Exception {
        SecurityContextImpl securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(new RememberMeAuthenticationToken(
                "TestUser", testUser, AuthorityUtils.createAuthorityList(roleUser)));
        SecurityContextHolder.setContext(securityContext);

        String errorMessage = userActivationCodeEmailConfirm;

        this.mockMvc.perform(put("/me/activate-code")
                        .param("activationCode", testActivationCode.getCode()))
                .andDo(print())
                .andExpect(view().name(templateUserPrivateArea))
                .andExpect(model().attribute("user", Matchers.any(User.class)))
                .andExpect(model().attribute("userActivationCodeEmailConfirm", Matchers.equalTo(errorMessage)))
                .andExpect(content().string(Matchers.containsString(errorMessage)))
                .andExpect(status().isOk());
    }

    @Test
    public void PUT_ActivateCodeAsUser_Failure_WithActivationCodeDoesNotExist() throws Exception {
        SecurityContextImpl securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(new RememberMeAuthenticationToken(
                "TestUser", testUserGAuth, AuthorityUtils.createAuthorityList(roleUser)));
        SecurityContextHolder.setContext(securityContext);

        String errorMessage = userInvalidActivationCode;

        this.mockMvc.perform(put("/me/activate-code")
                        .param("activationCode", testActivationCode.getCode().toUpperCase(Locale.ROOT)))
                .andDo(print())
                .andExpect(view().name(templateUserPrivateArea))
                .andExpect(model().attribute("user", Matchers.any(User.class)))
                .andExpect(model().attribute("userInvalidActivationCode", Matchers.equalTo(errorMessage)))
                .andExpect(content().string(Matchers.containsString(errorMessage)))
                .andExpect(status().isOk());
    }

    @Test
    public void PUT_ActivateCodeAsUser_Success() throws Exception {
        SecurityContextImpl securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(new RememberMeAuthenticationToken(
                "TestUser", testUserGAuth, AuthorityUtils.createAuthorityList(roleUser)));
        SecurityContextHolder.setContext(securityContext);

        this.mockMvc.perform(put("/me/activate-code")
                        .param("activationCode", testActivationCode.getCode()))
                .andDo(print())
                .andExpect(view().name(templateUserPrivateArea))
                .andExpect(status().isOk());
    }

    /////TEMP\\\\\\
    @Test
    @Sql(value = {"/forSelfGenerateCodeDeleteLater/clear-activation-codes.sql", "/forSelfGenerateCodeDeleteLater/clear-course-title.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void POST_SelfGenerateCodeAsUser_Failure_WithCourseTitleDoesNotExist() throws Exception {
        this.mockMvc.perform(post("/me/generate/activation-code"))
                .andDo(print())
                .andExpect(view().name(templateUserPrivateArea))
                .andExpect(model().attribute("user", Matchers.any(User.class)))
                .andExpect(status().isOk());
    }

    @Test
    @Sql(value = {"/forSelfGenerateCodeDeleteLater/clear-course-owner-role.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void POST_SelfGenerateCodeAsUser_Failure_WithRoleForCourseTitleDoesNotExist() throws Exception {
        this.mockMvc.perform(post("/me/generate/activation-code"))
                .andDo(print())
                .andExpect(view().name(templateUserPrivateArea))
                .andExpect(model().attribute("user", Matchers.any(User.class)))
                .andExpect(status().isOk());
    }

    @Test
    @Sql(value = {"/forSelfGenerateCodeDeleteLater/create-activation-code.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void POST_SelfGenerateCodeAsUser_Failure_WithCodeAlreadyGenerated() throws Exception {
        SecurityContextImpl securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(new RememberMeAuthenticationToken(
                "testUserGAuth", testUserGAuth, AuthorityUtils.createAuthorityList(roleUser)));
        SecurityContextHolder.setContext(securityContext);

        String errorMessage = userActivationCodeAlreadyGenerated;

        this.mockMvc.perform(post("/me/generate/activation-code"))
                .andDo(print())
                .andExpect(view().name(templateUserPrivateArea))
                .andExpect(model().attribute("user", Matchers.any(User.class)))
                .andExpect(model().attribute("userActivationCodeAlreadyGenerated", Matchers.equalTo(errorMessage)))
                .andExpect(content().string(Matchers.containsString(errorMessage)))
                .andExpect(status().isOk());
    }

    @Test
    public void POST_SelfGenerateCodeAsUser_Failure_WithEmailNotConfirmed() throws Exception {
        SecurityContextImpl securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(new RememberMeAuthenticationToken(
                "TestUser", testUser, AuthorityUtils.createAuthorityList(roleUser)));
        SecurityContextHolder.setContext(securityContext);

        String message = userActivationCodeEmailConfirm;

        this.mockMvc.perform(post("/me/generate/activation-code"))
                .andDo(print())
                .andExpect(view().name(templateUserPrivateArea))
                .andExpect(model().attribute("user", Matchers.any(User.class)))
                .andExpect(model().attribute("userActivationCodeEmailConfirm", Matchers.equalTo(message)))
                .andExpect(status().isOk());
    }

    @Test
    @Sql(value = {"/forSelfGenerateCodeDeleteLater/clear-activation-codes.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void POST_SelfGenerateCodeAsUser_Success() throws Exception {
        SecurityContextImpl securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(new RememberMeAuthenticationToken(
                "testUserGAuth", testUserGAuth, AuthorityUtils.createAuthorityList(roleUser)));
        SecurityContextHolder.setContext(securityContext);

        String message = userActivationCodeEmailSent;

        this.mockMvc.perform(post("/me/generate/activation-code"))
                .andDo(print())
                .andExpect(view().name(templateUserPrivateArea))
                .andExpect(model().attribute("user", Matchers.any(User.class)))
                .andExpect(model().attribute("userActivationCodeEmailSent", Matchers.equalTo(message)))
                .andExpect(status().isOk());
    }

    @Test
    public void POST_EmailRequest_FailureTokenAlreadyExistsByUserAndPurpose() throws Exception {
        SecurityContextImpl securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(new RememberMeAuthenticationToken(
                "testUser", testUser, AuthorityUtils.createAuthorityList(roleUser)));
        SecurityContextHolder.setContext(securityContext);

        String message = userEmailConfirmSent;

        this.mockMvc.perform(post("/me/email-request"))
                .andDo(print())
                .andExpect(view().name(templateUserPrivateArea))
                .andExpect(model().attribute("user", Matchers.any(User.class)))
                .andExpect(model().attribute("message", Matchers.equalTo(message)))
                .andExpect(status().isOk());
    }

    @Test
    @Sql(value = {"/clear-secure-tokens.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void POST_EmailRequest_Success() throws Exception {
        SecurityContextImpl securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(new RememberMeAuthenticationToken(
                "testAuthor", testAuthor, AuthorityUtils.createAuthorityList(roleUser)));
        SecurityContextHolder.setContext(securityContext);

        String message = userEmailConfirmSent;

        this.mockMvc.perform(post("/me/email-request"))
                .andDo(print())
                .andExpect(view().name(templateUserPrivateArea))
                .andExpect(model().attribute("user", Matchers.any(User.class)))
                .andExpect(model().attribute("message", Matchers.equalTo(message)))
                .andExpect(status().isOk());
    }

    @Test
    public void GET_EmailConfirm_Failure_WithNoToken() throws Exception {
        String errorMessage = emptyToken;

        this.mockMvc.perform(get("/me/email-confirm"))
                .andDo(print())
                .andExpect(view().name(templateGeneralEmailConfirm))
                .andExpect(model().attribute("tokenError", Matchers.equalTo(errorMessage)))
                .andExpect(status().isOk());
    }

    @Test
    public void GET_EmailConfirm_Failure_WithInvalidError() throws Exception {
        String errorMessage = invalidToken;

        this.mockMvc.perform(get("/me/email-confirm")
                        .param("token", testUser.getUsername()))
                .andDo(print())
                .andExpect(view().name(templateGeneralEmailConfirm))
                .andExpect(model().attribute("tokenError", Matchers.equalTo(errorMessage)))
                .andExpect(status().isOk());
    }

    @Test
    public void GET_EmailConfirm_Success() throws Exception {
        this.mockMvc.perform(get("/me/email-confirm")
                        .param("token", testSecureToken.getToken()))
                .andDo(print())
                .andExpect(view().name(templateGeneralEmailConfirm))
                .andExpect(model().attribute("token", Matchers.equalTo(testSecureToken.getToken())))
                .andExpect(model().attribute("email", Matchers.equalTo(testSecureToken.getUser().getEmail())))
                .andExpect(status().isOk());
    }

    @Test
    public void POST_EmailConfirm_Failure_WithInvalidToken() throws Exception {

        String message = invalidToken;

        this.mockMvc.perform(post("/me/email-confirm"))
                .andDo(print())
                .andExpect(view().name(templateUserPrivateArea))
                .andExpect(model().attribute("user", Matchers.any(User.class)))
                .andExpect(model().attribute("message", Matchers.equalTo(message)))
                .andExpect(status().isOk());
    }

    @Test
    public void POST_EmailConfirm_Success() throws Exception {

        String message = userEmailConfirmSuccess;

        this.mockMvc.perform(post("/me/email-confirm")
                        .param("token", testSecureToken.getToken()))
                .andDo(print())
                .andExpect(view().name(templateUserPrivateArea))
                .andExpect(model().attribute("user", Matchers.any(User.class)))
                .andExpect(model().attribute("message", Matchers.equalTo(message)))
                .andExpect(status().isOk());
    }
}