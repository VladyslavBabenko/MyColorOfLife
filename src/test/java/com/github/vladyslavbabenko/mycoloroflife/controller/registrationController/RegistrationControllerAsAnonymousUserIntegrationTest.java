package com.github.vladyslavbabenko.mycoloroflife.controller.registrationController;

import com.github.vladyslavbabenko.mycoloroflife.AbstractTest.AbstractControllerIntegrationTest;
import com.github.vladyslavbabenko.mycoloroflife.entity.User;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WithAnonymousUser
@DisplayName("Integration-level testing for RegistrationController as AnonymousUser")
@Sql(value = {"/create-test-values.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class RegistrationControllerAsAnonymousUserIntegrationTest extends AbstractControllerIntegrationTest {

    private User testUser;

    //Templates
    @Value("${template.general.registration}")
    String templateGeneralRegistration;


    //Messages
    @Value("${validation.user.email.not.valid}")
    String validationUserEmailNotValid;

    @Value("${user.exists.already}")
    String userExistsAlready;

    @Value("${user.password.mismatch}")
    String userPasswordMismatch;


    @BeforeEach
    void setUp() {
        super.setup();

        testUser = User.builder()
                .id(1)
                .name("TestUser")
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
                .andExpect(view().name(templateGeneralRegistration))
                .andExpect(model().attribute("user", Matchers.any(User.class)))
                .andExpect(status().isOk());
    }

    @Test
    public void POST_RegistrationPageAsAnonymousUser_WithErrors() throws Exception {
        String errorMessage = validationUserEmailNotValid;

        testUser.setEmail("TestUsermailcom");

        this.mockMvc.perform(post("/registration")
                        .param("name", testUser.getName())
                        .param("email", testUser.getEmail())
                        .param("password", testUser.getPassword())
                        .param("passwordConfirm", testUser.getPasswordConfirm()))
                .andDo(print())
                .andExpect(view().name(templateGeneralRegistration))
                .andExpect(content().string(Matchers.containsString(errorMessage)))
                .andExpect(status().isOk());
    }

    @Test
    public void POST_RegistrationPageAsAnonymousUser_WithPasswordMismatchError() throws Exception {
        String errorMessage = userPasswordMismatch;

        testUser.setPasswordConfirm(testUser.getPassword() + testUser.getPassword());

        this.mockMvc.perform(post("/registration")
                        .param("name", testUser.getName())
                        .param("email", testUser.getEmail())
                        .param("password", testUser.getPassword())
                        .param("passwordConfirm", testUser.getPasswordConfirm()))
                .andDo(print())
                .andExpect(view().name(templateGeneralRegistration))
                .andExpect(model().attribute("userPasswordMismatch", Matchers.equalTo(errorMessage)))
                .andExpect(content().string(Matchers.containsString(errorMessage)))
                .andExpect(status().isOk());
    }

    @Test
    public void POST_RegistrationPageAsAnonymousUser_WithSaveUserFailure() throws Exception {
        String errorMessage = userExistsAlready;

        this.mockMvc.perform(post("/registration")
                        .param("name", testUser.getName())
                        .param("email", testUser.getEmail())
                        .param("password", testUser.getPassword())
                        .param("passwordConfirm", testUser.getPasswordConfirm()))
                .andDo(print())
                .andExpect(view().name(templateGeneralRegistration))
                .andExpect(model().attribute("userExistsAlready", Matchers.equalTo(errorMessage)))
                .andExpect(content().string(Matchers.containsString(errorMessage)))
                .andExpect(status().isOk());
    }

    @Test
    @Sql(value = {"/clear-db.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void POST_RegistrationPageAsAnonymousUser_WithSaveUserSuccess() throws Exception {
        testUser.setName("NewTestUser");

        this.mockMvc.perform(post("/registration")
                        .param("id", String.valueOf(testUser.getId()))
                        .param("name", testUser.getName())
                        .param("email", testUser.getEmail())
                        .param("password", testUser.getPassword())
                        .param("passwordConfirm", testUser.getPasswordConfirm()))
                .andDo(print())
                .andExpect(redirectedUrl("/"))
                .andExpect(status().is3xxRedirection());
    }
}