package com.github.vladyslavbabenko.mycoloroflife.controller.generalController;

import com.github.vladyslavbabenko.mycoloroflife.controller.AbstractControllerIntegrationTest;
import org.fest.assertions.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockServletContext;
import org.springframework.security.test.context.support.WithAnonymousUser;

import javax.servlet.ServletContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WithAnonymousUser
@DisplayName("Integration-level testing for GeneralController as AnonymousUser")
class GeneralControllerForAnonymousUserIntegrationTest extends AbstractControllerIntegrationTest {

    @BeforeEach
    public void setup() {
        super.setup();
    }

    @Test
    public void isGeneralControllerIntegrationTestSetUpForTests() {
        ServletContext servletContext = webApplicationContext.getServletContext();

        Assertions.assertThat(servletContext).isNotNull().isInstanceOf(MockServletContext.class);
        Assertions.assertThat(webApplicationContext.getBean("generalController")).isNotNull();
    }

    @Test
    public void GET_MainPageAsAnonymousUser() throws Exception {
        this.mockMvc.perform(get("/"))
                .andExpect(view().name("generalTemplate/mainPage"))
                .andExpect(status().isOk());
    }

    @Test
    public void GET_LoginPageAsAnonymousUser() throws Exception {
        this.mockMvc.perform(get("/login"))
                .andExpect(view().name("generalTemplate/loginPage"))
                .andExpect(status().isOk());
    }

    @Test
    public void GET_AccessDeniedPageAsAnonymousUser() throws Exception {
        this.mockMvc.perform(get("/access-denied"))
                .andExpect(view().name("generalTemplate/accessDeniedPage"))
                .andExpect(status().isOk());
    }
}