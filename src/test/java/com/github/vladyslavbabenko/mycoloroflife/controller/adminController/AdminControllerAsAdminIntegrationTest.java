package com.github.vladyslavbabenko.mycoloroflife.controller.adminController;

import com.github.vladyslavbabenko.mycoloroflife.controller.AbstractControllerIntegrationTest;
import com.github.vladyslavbabenko.mycoloroflife.entity.User;
import org.fest.assertions.api.Assertions;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockServletContext;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;

import javax.servlet.ServletContext;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WithMockUser(username = "TestAdmin", roles = {"USER", "ADMIN"})
@DisplayName("Integration-level testing for AdminController as Admin")
@Sql(value = {"/create-test-values.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class AdminControllerAsAdminIntegrationTest extends AbstractControllerIntegrationTest {

    @BeforeEach
    void setUp() {
        super.setup();
    }

    @Test
    public void isArticleControllerIntegrationTestSetUpForTests() {
        ServletContext servletContext = webApplicationContext.getServletContext();

        Assertions.assertThat(servletContext).isNotNull().isInstanceOf(MockServletContext.class);
        Assertions.assertThat(webApplicationContext.getBean("adminController")).isNotNull();
    }

    @Test
    public void GET_AdminPanelPageAsAdmin() throws Exception {
        this.mockMvc.perform(get("/admin"))
                .andDo(print())
                .andExpect(view().name("adminTemplate/adminPanelPage"))
                .andExpect(model().attribute("listOfUsers", Matchers.any(List.class)))
                .andExpect(status().isOk());
    }

    @Test
    public void GET_AdminPanelPageAsAdmin_WithFindUserByIDFailure_InvalidInput() throws Exception {
        String errorMessage = "Недійсний ідентифікатор користувача";

        this.mockMvc.perform(get("/admin/findByID")
                        .param("userID", "text"))
                .andDo(print())
                .andExpect(view().name("adminTemplate/adminPanelPage"))
                .andExpect(model().attribute("listOfUsers", Matchers.any(List.class)))
                .andExpect(model().attribute("findIdError", Matchers.equalTo(errorMessage)))
                .andExpect(content().string(Matchers.containsString(errorMessage)))
                .andExpect(status().isOk());
    }

    @Test
    public void GET_AdminPanelPageAsAdmin_WithFindUserByIDFailure_InvalidID() throws Exception {
        String errorMessage = "Недійсний ідентифікатор користувача";

        this.mockMvc.perform(get("/admin/findByID")
                        .param("userID", "-1"))
                .andDo(print())
                .andExpect(view().name("adminTemplate/adminPanelPage"))
                .andExpect(model().attribute("listOfUsers", Matchers.any(List.class)))
                .andExpect(model().attribute("findIdError", Matchers.equalTo(errorMessage)))
                .andExpect(content().string(Matchers.containsString(errorMessage)))
                .andExpect(status().isOk());
    }

    @Test
    public void GET_AdminPanelPageAsAdmin_WithFindUserByIDSuccess() throws Exception {
        this.mockMvc.perform(get("/admin/findByID")
                        .param("userID", "1"))
                .andDo(print())
                .andExpect(view().name("adminTemplate/adminPanelPage"))
                .andExpect(model().attribute("listOfUsers", Matchers.any(User.class)))
                .andExpect(status().isOk());
    }

    @Test
    public void DELETE_UserByIDAsAdminFailure_InvalidID() throws Exception {
        String errorMessage = "Недійсний ідентифікатор користувача";

        this.mockMvc.perform(delete("/admin")
                        .param("userID", String.valueOf(Integer.MAX_VALUE)))
                .andDo(print())
                .andExpect(model().attribute("deleteIdError", Matchers.equalTo(errorMessage)))
                .andExpect(content().string(Matchers.containsString(errorMessage)))
                .andExpect(view().name("adminTemplate/adminPanelPage"))
                .andExpect(model().attribute("listOfUsers", Matchers.any(List.class)))
                .andExpect(status().isOk());
    }

    @Test
    public void DELETE_UserByIDAsAdminFailure_InvalidInput() throws Exception {
        String errorMessage = "Недійсний ідентифікатор користувача";

        this.mockMvc.perform(delete("/admin")
                        .param("userID", "text"))
                .andDo(print())
                .andExpect(model().attribute("deleteIdError", Matchers.equalTo(errorMessage)))
                .andExpect(content().string(Matchers.containsString(errorMessage)))
                .andExpect(view().name("adminTemplate/adminPanelPage"))
                .andExpect(model().attribute("listOfUsers", Matchers.any(List.class)))
                .andExpect(status().isOk());
    }

    @Test
    public void DELETE_UserByIDAsAdminSuccess() throws Exception {
        this.mockMvc.perform(delete("/admin")
                        .param("userID", "1"))
                .andDo(print())
                .andExpect(redirectedUrl("/admin"))
                .andExpect(status().is3xxRedirection());
    }
}