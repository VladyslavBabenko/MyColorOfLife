package com.github.vladyslavbabenko.mycoloroflife.controller.generalController;

import com.github.vladyslavbabenko.mycoloroflife.AbstractTest.AbstractControllerIntegrationTest;
import org.fest.assertions.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockServletContext;
import org.springframework.security.test.context.support.WithMockUser;

import javax.servlet.ServletContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WithMockUser(username = "TestUser", roles = "USER")
@DisplayName("Integration-level testing for GeneralController as User")
public class GeneralControllerAsUserIntegrationTest extends AbstractControllerIntegrationTest {

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
    public void GET_MainPageAsUser() throws Exception {
        this.mockMvc.perform(get("/"))
                .andDo(print())
                .andExpect(view().name(templateGeneralMain))
                .andExpect(status().isOk());
    }

    @Test
    public void GET_LoginPageAsUser() throws Exception {
        this.mockMvc.perform(get("/login"))
                .andDo(print())
                .andExpect(view().name(templateGeneralLogin))
                .andExpect(status().isOk());
    }

    @Test
    public void GET_AccessDeniedPageAsUser() throws Exception {
        this.mockMvc.perform(get("/access-denied"))
                .andDo(print())
                .andExpect(view().name(templateErrorAccessDenied))
                .andExpect(status().isOk());
    }
}