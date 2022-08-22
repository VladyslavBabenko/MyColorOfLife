package com.github.vladyslavbabenko.mycoloroflife.controller.adminController;

import com.github.vladyslavbabenko.mycoloroflife.controller.AbstractControllerIntegrationTest;
import com.github.vladyslavbabenko.mycoloroflife.entity.Course;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
    public void isAdminControllerIntegrationTestSetUpForTests() {
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

    @Test
    public void GET_CoursePageAsAdmin() throws Exception {
        this.mockMvc.perform(get("/admin/course"))
                .andDo(print())
                .andExpect(view().name("adminTemplate/courseAdminPage"))
                .andExpect(model().attribute("listOfCourses", Matchers.any(List.class)))
                .andExpect(status().isOk());
    }

    @Test
    public void GET_EditCoursePageAsAdmin() throws Exception {
        this.mockMvc.perform(get("/admin/course/1/edit"))
                .andDo(print())
                .andExpect(view().name("adminTemplate/editCoursePage"))
                .andExpect(model().attribute("course", Matchers.any(Course.class)))
                .andExpect(status().isOk());
    }

    @Test
    public void GET_NewCoursePageAsAdmin() throws Exception {
        this.mockMvc.perform(get("/admin/course/new"))
                .andDo(print())
                .andExpect(view().name("adminTemplate/addCoursePage"))
                .andExpect(model().attribute("course", Matchers.any(Course.class)))
                .andExpect(status().isOk());
    }

    @Test
    public void POST_AddCourseAsAdmin_WithErrors() throws Exception {
        String errorMessage = "Назва не повинна бути порожньою";

        this.mockMvc.perform(post("/admin/course")
                        .param("courseTitle", "")
                        .param("text", "First test text")
                        .param("page", "50"))
                .andDo(print())
                .andExpect(view().name("adminTemplate/addCoursePage"))
                .andExpect(content().string(Matchers.containsString(errorMessage)))
                .andExpect(status().isOk());
    }

    @Test
    public void POST_AddCourseAsAdmin_WithSameCourseExists() throws Exception {
        String errorMessage = "Така сторінка вже існує";

        this.mockMvc.perform(post("/admin/course")
                        .param("courseTitle", "Test")
                        .param("page", "1"))
                .andDo(print())
                .andExpect(view().name("adminTemplate/addCoursePage"))
                .andExpect(model().attribute("courseError", Matchers.equalTo(errorMessage)))
                .andExpect(content().string(Matchers.containsString(errorMessage)))
                .andExpect(status().isOk());
    }

    @Test
    @Sql(value = {"/clear-db.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void POST_AddCourseAsAdmin_WithSuccess() throws Exception {
        this.mockMvc.perform(post("/admin/course")
                        .param("courseTitle", "New Course")
                        .param("text", "First test text")
                        .param("page", "1"))
                .andDo(print())
                .andExpect(redirectedUrl("/admin/course"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    public void DELETE_CourseByIdAsAdminSuccess() throws Exception {
        this.mockMvc.perform(delete("/admin/course")
                        .param("courseID", "1"))
                .andDo(print())
                .andExpect(redirectedUrl("/admin/course"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    public void PUT_UpdateCourseAsAdmin_WithUpdateCourseFailure() throws Exception {
        String errorMessage = "Перевірте коректність введених даних";

        this.mockMvc.perform(put("/admin/course")
                        .param("id", "-1")
                        .param("courseTitle", "Test")
                        .param("page", "1"))
                .andDo(print())
                .andExpect(view().name("adminTemplate/editCoursePage"))
                .andExpect(model().attribute("courseError", Matchers.equalTo(errorMessage)))
                .andExpect(content().string(Matchers.containsString(errorMessage)))
                .andExpect(status().isOk());
    }

    @Test
    public void PUT_UpdateCourseAsAdmin_WithErrors() throws Exception {
        String errorMessage = "Введіть сторінку";

        this.mockMvc.perform(put("/admin/course")
                        .param("id", "1")
                        .param("courseTitle", "Test")
                        .param("page", ""))
                .andDo(print())
                .andExpect(view().name("adminTemplate/editCoursePage"))
                .andExpect(content().string(Matchers.containsString(errorMessage)))
                .andExpect(status().isOk());
    }

    @Test
    public void PUT_UpdateCourseAsAdmin_WithUpdateCourseSuccess() throws Exception {
        this.mockMvc.perform(put("/admin/course")
                        .param("id", "1")
                        .param("courseTitle", "Test")
                        .param("page", "23"))
                .andDo(print())
                .andExpect(redirectedUrl("/admin/course"))
                .andExpect(status().is3xxRedirection());
    }
}