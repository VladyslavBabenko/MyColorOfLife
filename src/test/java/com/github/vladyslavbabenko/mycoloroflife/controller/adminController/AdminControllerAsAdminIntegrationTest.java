package com.github.vladyslavbabenko.mycoloroflife.controller.adminController;

import com.github.vladyslavbabenko.mycoloroflife.controller.AbstractControllerIntegrationTest;
import com.github.vladyslavbabenko.mycoloroflife.entity.Course;
import com.github.vladyslavbabenko.mycoloroflife.entity.CourseTitle;
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

    private CourseTitle testCourseTitle;

    @BeforeEach
    void setUp() {
        testCourseTitle = CourseTitle.builder().id(1).title("Test").description("Test description").build();

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

        this.mockMvc.perform(get("/admin/find-by-id")
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

        this.mockMvc.perform(get("/admin/find-by-id")
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
        this.mockMvc.perform(get("/admin/find-by-id")
                        .param("userID", "1"))
                .andDo(print())
                .andExpect(view().name("adminTemplate/adminPanelPage"))
                .andExpect(model().attribute("listOfUsers", Matchers.any(User.class)))
                .andExpect(status().isOk());
    }

    @Test
    public void DELETE_UserByIDAsAdminFailure_InvalidID() throws Exception {
        String errorMessage = "Недійсний ідентифікатор користувача";

        this.mockMvc.perform(delete("/admin/delete")
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

        this.mockMvc.perform(delete("/admin/delete")
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
        this.mockMvc.perform(delete("/admin/delete")
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
    public void POST_AddCourseAsAdmin_WithEmptyCourseTitle() throws Exception {
        String errorMessage = "Вкажіть назву";

        this.mockMvc.perform(post("/admin/course/new")
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

        this.mockMvc.perform(post("/admin/course/new")
                        .param("courseTitle", testCourseTitle.getId().toString())
                        .param("page", "1"))
                .andDo(print())
                .andExpect(view().name("adminTemplate/addCoursePage"))
                .andExpect(model().attribute("courseError", Matchers.equalTo(errorMessage)))
                .andExpect(content().string(Matchers.containsString(errorMessage)))
                .andExpect(status().isOk());
    }

    @Test
    @Sql(value = {"/clear-all-courses.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void POST_AddCourseAsAdmin_WithSuccess() throws Exception {
        this.mockMvc.perform(post("/admin/course/new")
                        .param("courseTitle", testCourseTitle.getId().toString())
                        .param("text", "First test text")
                        .param("page", "25"))
                .andDo(print())
                .andExpect(redirectedUrl("/admin/course"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    public void DELETE_CourseByIdAsAdminSuccess() throws Exception {
        this.mockMvc.perform(delete("/admin/course/delete")
                        .param("courseID", "1"))
                .andDo(print())
                .andExpect(redirectedUrl("/admin/course"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    public void PUT_UpdateCourseAsAdmin_WithUpdateCourseFailure() throws Exception {
        String errorMessage = "Перевірте коректність введених даних";

        this.mockMvc.perform(put("/admin/course/edit")
                        .param("id", "-1")
                        .param("courseTitle", testCourseTitle.getId().toString())
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

        this.mockMvc.perform(put("/admin/course/edit")
                        .param("id", "1")
                        .param("courseTitle", testCourseTitle.getId().toString())
                        .param("page", ""))
                .andDo(print())
                .andExpect(view().name("adminTemplate/editCoursePage"))
                .andExpect(content().string(Matchers.containsString(errorMessage)))
                .andExpect(status().isOk());
    }

    @Test
    public void PUT_UpdateCourseAsAdmin_WithUpdateCourseSuccess() throws Exception {
        this.mockMvc.perform(put("/admin/course/edit")
                        .param("id", "1")
                        .param("courseTitle", testCourseTitle.getId().toString())
                        .param("page", "23"))
                .andDo(print())
                .andExpect(redirectedUrl("/admin/course"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    public void DELETE_ActivationCodeByCodeAsAdminSuccess() throws Exception {
        this.mockMvc.perform(delete("/admin/code/delete")
                        .param("activationCode", "Q5sxTc941iokNy8"))
                .andDo(print())
                .andExpect(redirectedUrl("/admin"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    public void DELETE_ActivationCodeByCodeAsAdmin_WithInvalidCode() throws Exception {
        String errorMessage = "Перевірте коректність введених даних";

        this.mockMvc.perform(delete("/admin/code/delete")
                        .param("activationCode", "qweqweqweqweqwe"))
                .andDo(print())
                .andExpect(view().name("adminTemplate/adminPanelPage"))
                .andExpect(model().attribute("codeError", Matchers.equalTo(errorMessage)))
                .andExpect(status().isOk());
    }

    @Test
    public void POST_CreateCodeAsAdmin_WithInvalidCourseTitleID() throws Exception {
        String errorMessage = "Такого курсу не існує";

        this.mockMvc.perform(post("/admin/code/new")
                        .param("userID", "1")
                        .param("courseTitleID", "-1"))
                .andDo(print())
                .andExpect(view().name("adminTemplate/adminPanelPage"))
                .andExpect(model().attribute("courseTitleIDError", Matchers.equalTo(errorMessage)))
                .andExpect(status().isOk());
    }

    @Test
    public void POST_CreateCodeAsAdmin_WithInvalidUserId() throws Exception {
        String errorMessage = "Користувач не знайдений";

        this.mockMvc.perform(post("/admin/code/new")
                        .param("userID", "-1")
                        .param("courseTitleID", testCourseTitle.getId().toString()))
                .andDo(print())
                .andExpect(view().name("adminTemplate/adminPanelPage"))
                .andExpect(model().attribute("userIDError", Matchers.equalTo(errorMessage)))
                .andExpect(status().isOk());
    }

    @Test
    public void POST_CreateCodeAsAdmin_WithCodeForUserAlreadyExists() throws Exception {
        String errorMessage = "Код цього курсу вже існує для користувача";

        this.mockMvc.perform(post("/admin/code/new")
                        .param("userID", "1")
                        .param("courseTitleID", testCourseTitle.getId().toString()))
                .andDo(print())
                .andExpect(view().name("adminTemplate/adminPanelPage"))
                .andExpect(model().attribute("codeForCourseExists", Matchers.equalTo(errorMessage)))
                .andExpect(status().isOk());
    }

    @Test
    @Sql(value = {"/clear-activation-codes.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void POST_CreateCodeAsAdmin_WithSuccess() throws Exception {
        this.mockMvc.perform(post("/admin/code/new")
                        .param("userID", "3")
                        .param("courseTitleID", testCourseTitle.getId().toString()))
                .andDo(print())
                .andExpect(redirectedUrl("/admin"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    public void GET_NewCourseTitlePageAsAdmin() throws Exception {
        this.mockMvc.perform(get("/admin/course-title/new"))
                .andDo(print())
                .andExpect(view().name("adminTemplate/addCourseTitlePage"))
                .andExpect(model().attribute("courseTitle", Matchers.any(CourseTitle.class)))
                .andExpect(status().isOk());
    }

    @Test
    public void POST_CreateCourseTitleAsAdmin_WithErrors() throws Exception {
        String errorMessage = "Вкажіть назву";

        this.mockMvc.perform(post("/admin/course-title/new")
                        .param("title", ""))
                .andDo(print())
                .andExpect(view().name("adminTemplate/addCourseTitlePage"))
                .andExpect(content().string(Matchers.containsString(errorMessage)))
                .andExpect(status().isOk());
    }

    @Test
    public void POST_CreateCourseTitleAsAdmin_WithCourseTitleAlreadyExists() throws Exception {
        String errorMessage = "Така назва вже існує";

        this.mockMvc.perform(post("/admin/course-title/new")
                        .param("title", testCourseTitle.getTitle())
                        .param("description", testCourseTitle.getDescription()))
                .andDo(print())
                .andExpect(view().name("adminTemplate/addCourseTitlePage"))
                .andExpect(model().attribute("courseTitleError", Matchers.equalTo(errorMessage)))
                .andExpect(content().string(Matchers.containsString(errorMessage)))
                .andExpect(status().isOk());
    }

    @Test
    @Sql(value = {"/clear-roles.sql", "/clear-course-titles.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void POST_CreateCourseTitleAsAdmin_WithSuccess() throws Exception {
        this.mockMvc.perform(post("/admin/course-title/new")
                        .param("title", "New " + testCourseTitle.getTitle())
                        .param("description", testCourseTitle.getDescription()))
                .andDo(print())
                .andExpect(redirectedUrl("/admin/course"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    public void DELETE_CourseTitleAsAdmin_WithInvalidID() throws Exception {
        String errorMessage = "Такого курсу не існує";

        this.mockMvc.perform(delete("/admin/course-title/delete")
                        .param("courseTitleID", "-1"))
                .andDo(print())
                .andExpect(view().name("adminTemplate/courseAdminPage"))
                .andExpect(model().attribute("courseTitleIDError", Matchers.equalTo(errorMessage)))
                .andExpect(model().attribute("listOfCourses", Matchers.any(List.class)))
                .andExpect(model().attribute("listOfCourseTitles", Matchers.any(List.class)))
                .andExpect(status().isOk());
    }

    @Test
    public void DELETE_CourseTitleAsAdmin_WithCourseWithThisTitleStillExists() throws Exception {
        String errorMessage = "Видаліть існуючі посилання на назву";

        this.mockMvc.perform(delete("/admin/course-title/delete")
                        .param("courseTitleID", "1"))
                .andDo(print())
                .andExpect(view().name("adminTemplate/courseAdminPage"))
                .andExpect(model().attribute("linksByCourseTitleError", Matchers.equalTo(errorMessage)))
                .andExpect(model().attribute("listOfCourses", Matchers.any(List.class)))
                .andExpect(model().attribute("listOfCourseTitles", Matchers.any(List.class)))
                .andExpect(status().isOk());
    }

    @Test
    @Sql(value = {"/clear-courses.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void DELETE_CourseTitleAsAdminSuccess() throws Exception {
        this.mockMvc.perform(delete("/admin/course-title/delete")
                        .param("courseTitleID", "2"))
                .andDo(print())
                .andExpect(redirectedUrl("/admin/course"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    public void GET_EditCourseTitlePageAsAdmin_WithInvalidID() throws Exception {
        this.mockMvc.perform(get("/admin/course-title/-1/edit"))
                .andDo(print())
                .andExpect(redirectedUrl("/admin/course"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    public void GET_EditCourseTitlePageAsAdmin_WithSuccess() throws Exception {
        this.mockMvc.perform(get("/admin/course-title/1/edit"))
                .andDo(print())
                .andExpect(view().name("adminTemplate/editCourseTitlePage"))
                .andExpect(model().attribute("courseTitle", Matchers.any(CourseTitle.class)))
                .andExpect(status().isOk());
    }

    @Test
    public void PUT_UpdateCourseTitleAsAdmin_WithInvaldiID() throws Exception {
        String errorMessage = "Перевірте коректність введених даних";

        this.mockMvc.perform(put("/admin/course-title/edit")
                        .param("id", "-1")
                        .param("title", testCourseTitle.getTitle())
                        .param("description", testCourseTitle.getDescription()))
                .andDo(print())
                .andExpect(view().name("adminTemplate/editCourseTitlePage"))
                .andExpect(content().string(Matchers.containsString(errorMessage)))
                .andExpect(model().attribute("courseTitleError", errorMessage))
                .andExpect(status().isOk());
    }

    @Test
    public void PUT_UpdateCourseTitleAsAdmin_WithErrors() throws Exception {
        String errorMessage = "Вкажіть назву";

        this.mockMvc.perform(put("/admin/course-title/edit")
                        .param("id", "-1")
                        .param("title", ""))
                .andDo(print())
                .andExpect(view().name("adminTemplate/editCourseTitlePage"))
                .andExpect(content().string(Matchers.containsString(errorMessage)))
                .andExpect(status().isOk());
    }

    @Test
    public void PUT_UpdateCourseTitleAsAdmin_WithUpdateCourseSuccess() throws Exception {
        this.mockMvc.perform(put("/admin/course-title/edit")
                        .param("id", "1")
                        .param("courseTitle", testCourseTitle.getId().toString()))
                .andDo(print())
                .andExpect(redirectedUrl("/admin/course"))
                .andExpect(status().is3xxRedirection());
    }
}