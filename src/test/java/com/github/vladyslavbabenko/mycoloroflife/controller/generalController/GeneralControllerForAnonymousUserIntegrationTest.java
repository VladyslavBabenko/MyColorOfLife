package com.github.vladyslavbabenko.mycoloroflife.controller.generalController;

import com.github.vladyslavbabenko.mycoloroflife.AbstractTest.AbstractControllerIntegrationTest;
import org.fest.assertions.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockServletContext;
import org.springframework.security.test.context.support.WithAnonymousUser;

import javax.servlet.ServletContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WithAnonymousUser
@DisplayName("Integration-level testing for GeneralController as AnonymousUser")
class GeneralControllerForAnonymousUserIntegrationTest extends AbstractControllerIntegrationTest {

    //Templates
    @Value("${template.general.main}")
    String templateGeneralMain;

    @Value("${template.general.login}")
    String templateGeneralLogin;

    @Value("${template.error.access-denied}")
    String templateErrorAccessDenied;


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
                .andExpect(view().name(templateGeneralMain))
                .andExpect(status().isOk());
    }

    @Test
    public void GET_LoginPageAsAnonymousUser() throws Exception {
        this.mockMvc.perform(get("/login"))
                .andExpect(view().name(templateGeneralLogin))
                .andExpect(status().isOk());
    }

    @Test
    public void GET_AccessDeniedPageAsAnonymousUser() throws Exception {
        this.mockMvc.perform(get("/access-denied"))
                .andExpect(view().name(templateErrorAccessDenied))
                .andExpect(status().isOk());
    }
}