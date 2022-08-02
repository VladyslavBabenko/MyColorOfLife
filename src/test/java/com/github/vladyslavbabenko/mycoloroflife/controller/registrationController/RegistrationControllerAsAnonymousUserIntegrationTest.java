package com.github.vladyslavbabenko.mycoloroflife.controller.registrationController;

import com.github.vladyslavbabenko.mycoloroflife.controller.AbstractControllerIntegrationTest;
import com.github.vladyslavbabenko.mycoloroflife.entity.User;
import org.fest.assertions.api.Assertions;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockServletContext;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.context.jdbc.Sql;

import javax.servlet.ServletContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WithAnonymousUser
@DisplayName("Integration-level testing for RegistrationController as AnonymousUser")
@Sql(value = {"/create-user-before.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class RegistrationControllerAsAnonymousUserIntegrationTest extends AbstractControllerIntegrationTest {

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
                .build();
    }

    @Test
    public void isRegistrationControllerIntegrationTestSetUpForTests() {
        ServletContext servletContext = webApplicationContext.getServletContext();

        Assertions.assertThat(servletContext).isNotNull().isInstanceOf(MockServletContext.class);
        Assertions.assertThat(webApplicationContext.getBean("registrationController")).isNotNull();
    }

    @Test
    public void GET_RegistrationPageAsAnonymousUser() throws Exception {
        this.mockMvc.perform(get("/registration"))
                .andDo(print())
                .andExpect(view().name("generalTemplate/registrationPage"))
                .andExpect(model().attribute("user", Matchers.any(User.class)))
                .andExpect(status().isOk());
    }

    @Test
    public void POST_RegistrationPageAsAnonymousUser_WithErrors() throws Exception {
        String errorText = "Пошта має бути валідною";

        testUser.setEmail("TestUsermailcom");

        this.mockMvc.perform(post("/registration")
                        .param("username", testUser.getUsername())
                        .param("email", testUser.getEmail())
                        .param("password", testUser.getPassword())
                        .param("passwordConfirm", testUser.getPasswordConfirm()))
                .andDo(print())
                .andExpect(view().name("generalTemplate/registrationPage"))
                .andExpect(content().string(Matchers.containsString(errorText)))
                .andExpect(status().isOk());
    }

    @Test
    public void POST_RegistrationPageAsAnonymousUser_WithPasswordMismatchError() throws Exception {
        String errorText = "Паролі не співпадають";

        testUser.setPasswordConfirm(testUser.getPassword() + testUser.getPassword());

        this.mockMvc.perform(post("/registration")
                        .param("username", testUser.getUsername())
                        .param("email", testUser.getEmail())
                        .param("password", testUser.getPassword())
                        .param("passwordConfirm", testUser.getPasswordConfirm()))
                .andDo(print())
                .andExpect(view().name("generalTemplate/registrationPage"))
                .andExpect(model().attribute("passwordMismatchError", Matchers.equalTo(errorText)))
                .andExpect(content().string(Matchers.containsString(errorText)))
                .andExpect(status().isOk());
    }

    @Test
    public void POST_RegistrationPageAsAnonymousUser_WithSaveUserFailure() throws Exception {
        String errorText = "Цей користувач уже існує";

        this.mockMvc.perform(post("/registration")
                        .param("username", testUser.getUsername())
                        .param("email", testUser.getEmail())
                        .param("password", testUser.getPassword())
                        .param("passwordConfirm", testUser.getPasswordConfirm()))
                .andDo(print())
                .andExpect(view().name("generalTemplate/registrationPage"))
                .andExpect(model().attribute("saveUserError", Matchers.equalTo(errorText)))
                .andExpect(content().string(Matchers.containsString(errorText)))
                .andExpect(status().isOk());
    }

    @Test
    public void POST_RegistrationPageAsAnonymousUser_WithSaveUserSuccess() throws Exception {
        testUser.setUsername("NewTestUser");

        this.mockMvc.perform(post("/registration")
                        .param("username", testUser.getUsername())
                        .param("email", testUser.getEmail())
                        .param("password", testUser.getPassword())
                        .param("passwordConfirm", testUser.getPasswordConfirm()))
                .andDo(print())
                .andExpect(redirectedUrl("/"))
                .andExpect(status().is3xxRedirection());
    }
}