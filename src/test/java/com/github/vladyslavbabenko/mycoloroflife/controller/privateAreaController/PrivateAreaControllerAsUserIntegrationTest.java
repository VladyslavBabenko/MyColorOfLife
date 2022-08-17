package com.github.vladyslavbabenko.mycoloroflife.controller.privateAreaController;

import com.github.vladyslavbabenko.mycoloroflife.controller.AbstractControllerIntegrationTest;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WithMockUser(username = "TestUser", roles = "USER")
@DisplayName("Integration-level testing for PrivateAreaControllerController as User")
@Sql(value = {"/create-test-values.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class PrivateAreaControllerAsUserIntegrationTest extends AbstractControllerIntegrationTest {

    private User testUser;

    @BeforeEach
    void setUp() {
        super.setup();
        testUser = User.builder()
                .id(1)
                .username("TestUser")
                .email("TestUser@mail.com")
                .password("123456")
                .passwordConfirm("123456")
                .registrationType(UserRegistrationType.REGISTRATION_FORM)
                .build();
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
                .andExpect(model().attribute("passwordMismatchError", Matchers.equalTo(errorMessage)))
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
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/me"));
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
                .andExpect(model().attribute("oldPasswordError", Matchers.equalTo(errorMessage)))
                .andExpect(content().string(Matchers.containsString(errorMessage)))
                .andExpect(status().isOk());
    }


    @Test
    public void PATCH_ChangePasswordPageAsUser_WithChangePasswordFailure() throws Exception {
        testUser.setId(-1);

        SecurityContextImpl securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(new RememberMeAuthenticationToken(
                "TestUser", testUser, AuthorityUtils.createAuthorityList("ROLE_USER")));
        SecurityContextHolder.setContext(securityContext);

        String oldPassword = testUser.getPassword();
        System.out.println(testUser);
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
                .andExpect(model().attribute("changePasswordError", Matchers.equalTo(errorMessage)))
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
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/me"));
    }

    @Test
    public void PATCH_EditPageAsUser_WithError() throws Exception {
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
}