package com.github.vladyslavbabenko.mycoloroflife.controller.adminController;

import com.github.vladyslavbabenko.mycoloroflife.AbstractTest.AbstractControllerIntegrationTest;
import com.github.vladyslavbabenko.mycoloroflife.entity.Course;
import com.github.vladyslavbabenko.mycoloroflife.entity.CourseTitle;
import com.github.vladyslavbabenko.mycoloroflife.entity.User;
import org.fest.assertions.api.Assertions;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
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

    //Templates
    @Value("${template.admin.panel.user}")
    String templateUserAdminPanel;

    @Value("${template.admin.panel.course}")
    String templateCourseAdminPanel;

    @Value("${template.admin.course.edit}")
    String templateAdminCourseEdit;

    @Value("${template.admin.course.title.add}")
    String templateAdminCourseTitleAdd;

    @Value("${template.admin.course.add}")
    String templateAdminCourseAdd;

    @Value("${template.admin.course.title.edit}")
    String templateAdminCourseTitleEdit;


    //Messages
    @Value("${user.invalid.id}")
    String userInvalidId;

    @Value("${course.page.exists}")
    String coursePageExists;

    @Value("${course.page.not.exists}")
    String coursePageNotExists;

    @Value("${invalid.input}")
    String invalidInput;

    @Value("${validation.title.not.empty}")
    String validationTitleNotEmpty;

    @Value("${course.exists.links}")
    String courseExistsLinks;

    @Value("${course.title.not.exists}")
    String courseTitleNotExists;

    @Value("${course.title.exists}")
    String courseTitleExists;

    @Value("${validation.page.not.empty}")
    String validationPageNotEmpty;

    @Value("${user.not.found}")
    String userNotFound;

    @Value("${user.activation-code.exists}")
    String userActivationCodeExists;


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
                .andExpect(view().name(templateUserAdminPanel))
                .andExpect(model().attribute("listOfUsers", Matchers.any(List.class)))
                .andExpect(status().isOk());
    }

    @Test
    public void GET_AdminPanelPageAsAdmin_WithFindUserByIDFailure_InvalidInput() throws Exception {
        String errorMessage = userInvalidId;

        this.mockMvc.perform(get("/admin/find-by-id")
                        .param("userID", "text"))
                .andDo(print())
                .andExpect(view().name(templateUserAdminPanel))
                .andExpect(model().attribute("listOfUsers", Matchers.any(List.class)))
                .andExpect(model().attribute("getUserInvalidID", Matchers.equalTo(errorMessage)))
                .andExpect(content().string(Matchers.containsString(errorMessage)))
                .andExpect(status().isOk());
    }

    @Test
    public void GET_AdminPanelPageAsAdmin_WithFindUserByIDFailure_InvalidID() throws Exception {
        String errorMessage = userInvalidId;

        this.mockMvc.perform(get("/admin/find-by-id")
                        .param("userID", "-1"))
                .andDo(print())
                .andExpect(view().name(templateUserAdminPanel))
                .andExpect(model().attribute("listOfUsers", Matchers.any(List.class)))
                .andExpect(model().attribute("getUserInvalidID", Matchers.equalTo(errorMessage)))
                .andExpect(content().string(Matchers.containsString(errorMessage)))
                .andExpect(status().isOk());
    }

    @Test
    public void GET_AdminPanelPageAsAdmin_WithFindUserByIDSuccess() throws Exception {
        this.mockMvc.perform(get("/admin/find-by-id")
                        .param("userID", "1"))
                .andDo(print())
                .andExpect(view().name(templateUserAdminPanel))
                .andExpect(model().attribute("listOfUsers", Matchers.any(User.class)))
                .andExpect(status().isOk());
    }

    @Test
    public void DELETE_UserByIDAsAdminFailure_InvalidID() throws Exception {
        String errorMessage = userInvalidId;

        this.mockMvc.perform(delete("/admin/delete")
                        .param("userID", String.valueOf(Integer.MAX_VALUE)))
                .andDo(print())
                .andExpect(model().attribute("deleteUserInvalidID", Matchers.equalTo(errorMessage)))
                .andExpect(content().string(Matchers.containsString(errorMessage)))
                .andExpect(view().name(templateUserAdminPanel))
                .andExpect(model().attribute("listOfUsers", Matchers.any(List.class)))
                .andExpect(status().isOk());
    }

    @Test
    public void DELETE_UserByIDAsAdminFailure_InvalidInput() throws Exception {
        String errorMessage = userInvalidId;

        this.mockMvc.perform(delete("/admin/delete")
                        .param("userID", "text"))
                .andDo(print())
                .andExpect(model().attribute("deleteUserInvalidID", Matchers.equalTo(errorMessage)))
                .andExpect(content().string(Matchers.containsString(errorMessage)))
                .andExpect(view().name(templateUserAdminPanel))
                .andExpect(model().attribute("listOfUsers", Matchers.any(List.class)))
                .andExpect(status().isOk());
    }

    @Test
    public void DELETE_UserByIDAsAdminSuccess() throws Exception {
        this.mockMvc.perform(delete("/admin/delete")
                        .param("userID", "1"))
                .andDo(print())
                .andExpect(view().name(templateUserAdminPanel))
                .andExpect(status().isOk());
    }

    @Test
    public void GET_CoursePageAsAdmin() throws Exception {
        this.mockMvc.perform(get("/admin/course"))
                .andDo(print())
                .andExpect(view().name(templateCourseAdminPanel))
                .andExpect(model().attribute("listOfCourses", Matchers.any(List.class)))
                .andExpect(status().isOk());
    }

    @Test
    public void GET_EditCoursePageAsAdmin() throws Exception {
        this.mockMvc.perform(get("/admin/course/1/edit"))
                .andDo(print())
                .andExpect(view().name(templateAdminCourseEdit))
                .andExpect(model().attribute("course", Matchers.any(Course.class)))
                .andExpect(status().isOk());
    }

    @Test
    public void GET_NewCoursePageAsAdmin() throws Exception {
        this.mockMvc.perform(get("/admin/course/new"))
                .andDo(print())
                .andExpect(view().name(templateAdminCourseAdd))
                .andExpect(model().attribute("course", Matchers.any(Course.class)))
                .andExpect(status().isOk());
    }

    @Test
    public void POST_AddCourseAsAdmin_WithEmptyCourseTitle() throws Exception {
        String errorMessage = validationTitleNotEmpty;

        this.mockMvc.perform(post("/admin/course/new")
                        .param("courseTitle", "")
                        .param("text", "First test text")
                        .param("page", "50"))
                .andDo(print())
                .andExpect(view().name(templateAdminCourseAdd))
                .andExpect(content().string(Matchers.containsString(errorMessage)))
                .andExpect(status().isOk());
    }

    @Test
    public void POST_AddCourseAsAdmin_WithSameCourseExists() throws Exception {
        String errorMessage = coursePageExists;

        this.mockMvc.perform(post("/admin/course/new")
                        .param("courseTitle", testCourseTitle.getId().toString())
                        .param("page", "1"))
                .andDo(print())
                .andExpect(view().name(templateAdminCourseAdd))
                .andExpect(model().attribute("courseExistsPage", Matchers.equalTo(errorMessage)))
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
                .andExpect(view().name(templateCourseAdminPanel))
                .andExpect(status().isOk());
    }

    @Test
    public void DELETE_CourseByIdAsAdmin_Failure_With_CoursePageNotFound() throws Exception {
        String errorMessage = coursePageNotExists;

        this.mockMvc.perform(delete("/admin/course/delete")
                        .param("courseID", "100000000"))
                .andDo(print())
                .andExpect(model().attribute("coursePageNotFound", Matchers.equalTo(errorMessage)))
                .andExpect(content().string(Matchers.containsString(errorMessage)))
                .andExpect(view().name(templateCourseAdminPanel))
                .andExpect(status().isOk());
    }

    @Test
    public void DELETE_CourseByIdAsAdminSuccess() throws Exception {
        this.mockMvc.perform(delete("/admin/course/delete")
                        .param("courseID", "1"))
                .andDo(print())
                .andExpect(view().name(templateCourseAdminPanel))
                .andExpect(status().isOk());
    }

    @Test
    public void PUT_UpdateCourseAsAdmin_WithUpdateCourseFailure() throws Exception {
        String errorMessage = invalidInput;

        this.mockMvc.perform(put("/admin/course/edit")
                        .param("id", "-1")
                        .param("courseTitle", testCourseTitle.getId().toString())
                        .param("page", "1"))
                .andDo(print())
                .andExpect(view().name(templateAdminCourseEdit))
                .andExpect(model().attribute("courseInvalidInput", Matchers.equalTo(errorMessage)))
                .andExpect(content().string(Matchers.containsString(errorMessage)))
                .andExpect(status().isOk());
    }

    @Test
    public void PUT_UpdateCourseAsAdmin_WithErrors() throws Exception {
        String errorMessage = validationPageNotEmpty;

        this.mockMvc.perform(put("/admin/course/edit")
                        .param("id", "1")
                        .param("courseTitle", testCourseTitle.getId().toString())
                        .param("page", ""))
                .andDo(print())
                .andExpect(view().name(templateAdminCourseEdit))
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
                .andExpect(view().name(templateCourseAdminPanel))
                .andExpect(status().isOk());
    }

    @Test
    public void DELETE_ActivationCodeByCodeAsAdminSuccess() throws Exception {
        this.mockMvc.perform(delete("/admin/code/delete")
                        .param("activationCode", "Q5sxTc941iokNy8"))
                .andDo(print())
                .andExpect(view().name(templateUserAdminPanel))
                .andExpect(status().isOk());
    }

    @Test
    public void DELETE_ActivationCodeByCodeAsAdmin_WithInvalidCode() throws Exception {
        String errorMessage = invalidInput;

        this.mockMvc.perform(delete("/admin/code/delete")
                        .param("activationCode", "qweqweqweqweqwe"))
                .andDo(print())
                .andExpect(view().name(templateUserAdminPanel))
                .andExpect(model().attribute("codeInvalidInput", Matchers.equalTo(errorMessage)))
                .andExpect(status().isOk());
    }

    @Test
    public void POST_CreateCodeAsAdmin_WithInvalidCourseTitleID() throws Exception {
        String errorMessage = courseTitleNotExists;

        this.mockMvc.perform(post("/admin/code/new")
                        .param("userID", "1")
                        .param("courseTitleID", "-1"))
                .andDo(print())
                .andExpect(view().name(templateUserAdminPanel))
                .andExpect(model().attribute("courseNotFound", Matchers.equalTo(errorMessage)))
                .andExpect(status().isOk());
    }

    @Test
    public void POST_CreateCodeAsAdmin_WithInvalidUserId() throws Exception {
        String errorMessage = userNotFound;

        this.mockMvc.perform(post("/admin/code/new")
                        .param("userID", "-1")
                        .param("courseTitleID", testCourseTitle.getId().toString()))
                .andDo(print())
                .andExpect(view().name(templateUserAdminPanel))
                .andExpect(model().attribute("userNotFound", Matchers.equalTo(errorMessage)))
                .andExpect(status().isOk());
    }

    @Test
    public void POST_CreateCodeAsAdmin_WithCodeForUserAlreadyExists() throws Exception {
        String errorMessage = userActivationCodeExists;

        this.mockMvc.perform(post("/admin/code/new")
                        .param("userID", "1")
                        .param("courseTitleID", testCourseTitle.getId().toString()))
                .andDo(print())
                .andExpect(view().name(templateUserAdminPanel))
                .andExpect(model().attribute("userActivationCodeExists", Matchers.equalTo(errorMessage)))
                .andExpect(status().isOk());
    }

    @Test
    @Sql(value = {"/clear-activation-codes.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void POST_CreateCodeAsAdmin_WithSuccess() throws Exception {
        this.mockMvc.perform(post("/admin/code/new")
                        .param("userID", "3")
                        .param("courseTitleID", testCourseTitle.getId().toString()))
                .andDo(print())
                .andExpect(view().name(templateUserAdminPanel))
                .andExpect(status().isOk());
    }

    @Test
    public void GET_NewCourseTitlePageAsAdmin() throws Exception {
        this.mockMvc.perform(get("/admin/course-title/new"))
                .andDo(print())
                .andExpect(view().name(templateAdminCourseTitleAdd))
                .andExpect(model().attribute("courseTitle", Matchers.any(CourseTitle.class)))
                .andExpect(status().isOk());
    }

    @Test
    public void POST_CreateCourseTitleAsAdmin_WithErrors() throws Exception {
        String errorMessage = validationTitleNotEmpty;

        this.mockMvc.perform(post("/admin/course-title/new")
                        .param("title", ""))
                .andDo(print())
                .andExpect(view().name(templateAdminCourseTitleAdd))
                .andExpect(content().string(Matchers.containsString(errorMessage)))
                .andExpect(status().isOk());
    }

    @Test
    public void POST_CreateCourseTitleAsAdmin_WithCourseTitleAlreadyExists() throws Exception {
        String errorMessage = courseTitleExists;

        this.mockMvc.perform(post("/admin/course-title/new")
                        .param("title", testCourseTitle.getTitle())
                        .param("description", testCourseTitle.getDescription()))
                .andDo(print())
                .andExpect(view().name(templateAdminCourseTitleAdd))
                .andExpect(model().attribute("courseTitleExists", Matchers.equalTo(errorMessage)))
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
                .andExpect(view().name(templateCourseAdminPanel))
                .andExpect(status().isOk());
    }

    @Test
    public void DELETE_CourseTitleAsAdmin_WithInvalidID() throws Exception {
        String errorMessage = courseTitleNotExists;

        this.mockMvc.perform(delete("/admin/course-title/delete")
                        .param("courseTitleID", "-1"))
                .andDo(print())
                .andExpect(view().name(templateCourseAdminPanel))
                .andExpect(model().attribute("courseNotFound", Matchers.equalTo(errorMessage)))
                .andExpect(model().attribute("listOfCourses", Matchers.any(List.class)))
                .andExpect(model().attribute("listOfCourseTitles", Matchers.any(List.class)))
                .andExpect(status().isOk());
    }

    @Test
    public void DELETE_CourseTitleAsAdmin_WithCourseWithThisTitleStillExists() throws Exception {
        String errorMessage = courseExistsLinks;

        this.mockMvc.perform(delete("/admin/course-title/delete")
                        .param("courseTitleID", "1"))
                .andDo(print())
                .andExpect(view().name(templateCourseAdminPanel))
                .andExpect(model().attribute("courseLinksExists", Matchers.equalTo(errorMessage)))
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
                .andExpect(view().name(templateCourseAdminPanel))
                .andExpect(status().isOk());
    }

    @Test
    public void GET_EditCourseTitlePageAsAdmin_WithInvalidID() throws Exception {
        this.mockMvc.perform(get("/admin/course-title/-1/edit"))
                .andDo(print())
                .andExpect(view().name(templateCourseAdminPanel))
                .andExpect(status().isOk());
    }

    @Test
    public void GET_EditCourseTitlePageAsAdmin_WithSuccess() throws Exception {
        this.mockMvc.perform(get("/admin/course-title/1/edit"))
                .andDo(print())
                .andExpect(view().name(templateAdminCourseTitleEdit))
                .andExpect(model().attribute("courseTitle", Matchers.any(CourseTitle.class)))
                .andExpect(status().isOk());
    }

    @Test
    public void PUT_UpdateCourseTitleAsAdmin_WithInvaldiID() throws Exception {
        String errorMessage = invalidInput;

        this.mockMvc.perform(put("/admin/course-title/edit")
                        .param("id", "-1")
                        .param("title", testCourseTitle.getTitle())
                        .param("description", testCourseTitle.getDescription()))
                .andDo(print())
                .andExpect(view().name(templateAdminCourseTitleEdit))
                .andExpect(content().string(Matchers.containsString(errorMessage)))
                .andExpect(model().attribute("courseTitleInvalidInput", errorMessage))
                .andExpect(status().isOk());
    }

    @Test
    public void PUT_UpdateCourseTitleAsAdmin_WithErrors() throws Exception {
        String errorMessage = validationTitleNotEmpty;

        this.mockMvc.perform(put("/admin/course-title/edit")
                        .param("id", "-1")
                        .param("title", ""))
                .andDo(print())
                .andExpect(view().name(templateAdminCourseTitleEdit))
                .andExpect(content().string(Matchers.containsString(errorMessage)))
                .andExpect(status().isOk());
    }

    @Test
    public void PUT_UpdateCourseTitleAsAdmin_WithUpdateCourseSuccess() throws Exception {
        this.mockMvc.perform(put("/admin/course-title/edit")
                        .param("id", "1")
                        .param("courseTitle", testCourseTitle.getId().toString()))
                .andDo(print())
                .andExpect(view().name(templateCourseAdminPanel))
                .andExpect(status().isOk());
    }
}