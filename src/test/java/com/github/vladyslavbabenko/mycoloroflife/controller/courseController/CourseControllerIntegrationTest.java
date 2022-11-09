package com.github.vladyslavbabenko.mycoloroflife.controller.courseController;

import com.github.vladyslavbabenko.mycoloroflife.AbstractTest.AbstractControllerIntegrationTest;
import com.github.vladyslavbabenko.mycoloroflife.entity.Course;
import com.github.vladyslavbabenko.mycoloroflife.entity.CourseTitle;
import com.github.vladyslavbabenko.mycoloroflife.entity.User;
import com.github.vladyslavbabenko.mycoloroflife.enumeration.UserRegistrationType;
import org.fest.assertions.api.Assertions;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockServletContext;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;

import javax.servlet.ServletContext;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WithMockUser(username = "TestUser", roles = {"USER", "COURSE_OWNER_TEST"})
@DisplayName("Integration-level testing for PrivateAreaControllerController as User")
@Sql(value = {"/create-test-values.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class CourseControllerIntegrationTest extends AbstractControllerIntegrationTest {
    private User testUser;
    private CourseTitle testCourseTitle;
    private Course testCourse;


    //Templates
    @Value("${template.course.all}")
    String templateCourseAll;

    @Value("${template.course.page}")
    String templateCoursePage;

    @Value("${template.error.404}")
    String templateError404;

    @Value("${template.course.main}")
    String templateCourseMain;

    @Value("${template.error.access-denied}")
    String templateErrorAccessDenied;


    //Messages
    @Value("${user.course.too-early}")
    String userCourseTooEarly;

    @Value("${role.user}")
    String roleUser;

    @Value("${role.course.owner}")
    String roleCourseOwner;

    String roleCourseOwnerTest;

    @BeforeEach
    void setUp() {
        super.setup();

        roleCourseOwnerTest = roleCourseOwner + "TEST";

        testUser = User.builder()
                .id(1)
                .name("TestUser")
                .email("TestUser@mail.com")
                .password("123456")
                .passwordConfirm("123456")
                .registrationType(UserRegistrationType.REGISTRATION_FORM)
                .build();

        testCourseTitle = CourseTitle.builder().id(1).title("Test").description("Test description").build();

        testCourse = Course.builder().id(1).page(1).text("Test Text 1").courseTitle(testCourseTitle).build();
    }

    @Test
    public void isCourseControllerIntegrationTestSetUpForTests() {
        ServletContext servletContext = webApplicationContext.getServletContext();

        Assertions.assertThat(servletContext).isNotNull().isInstanceOf(MockServletContext.class);
        Assertions.assertThat(webApplicationContext.getBean("courseController")).isNotNull();
    }

    @Test
    public void GET_EventsPageAsAuthor_WithKeyword() throws Exception {
        this.mockMvc.perform(get("/course")
                        .param("keyword", "Test"))
                .andDo(print())
                .andExpect(view().name(templateCourseAll))
                .andExpect(model().attribute("listOfCourseTitles", Matchers.any(List.class)))
                .andExpect(model().attribute("pageID", Matchers.any(Integer.class)))
                .andExpect(model().attribute("numberOfPages", Matchers.any(int[].class)))
                .andExpect(status().isOk());
    }

    @Test
    public void GET_CoursesPageAsCourseOwner() throws Exception {
        this.mockMvc.perform(get("/course"))
                .andDo(print())
                .andExpect(view().name(templateCourseAll))
                .andExpect(model().attribute("listOfCourseTitles", Matchers.any(List.class)))
                .andExpect(model().attribute("pageID", Matchers.any(Integer.class)))
                .andExpect(model().attribute("numberOfPages", Matchers.any(int[].class)))
                .andExpect(status().isOk());
    }

    @Test
    public void GET_MainCoursesPageAsCourseOwner_Failure_InvalidTitle() throws Exception {
        this.mockMvc.perform(get("/course/" + testCourseTitle.getTitle().repeat(2)))
                .andDo(print())
                .andExpect(view().name(templateError404))
                .andExpect(status().isOk());
    }

    @Test
    public void GET_MainCoursesPageAsCourseOwner_Success() throws Exception {
        SecurityContextImpl securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(new RememberMeAuthenticationToken(
                "TestUser", testUser, AuthorityUtils.createAuthorityList(roleUser, roleCourseOwnerTest)));
        SecurityContextHolder.setContext(securityContext);

        this.mockMvc.perform(get("/course/Test"))
                .andDo(print())
                .andExpect(view().name(templateCourseMain))
                .andExpect(model().attribute("lastVisitedPage", Matchers.any(Integer.class)))
                .andExpect(model().attribute("courseTitle", Matchers.any(CourseTitle.class)))
                .andExpect(model().attribute("courseList", Matchers.any(List.class)))
                .andExpect(status().isOk());
    }

    @Test
    public void GET_CoursePageAsCourseOwner_Failure_InvalidCourseTitle() throws Exception {
        SecurityContextImpl securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(new RememberMeAuthenticationToken(
                "TestUser", testUser, AuthorityUtils.createAuthorityList(roleUser, roleCourseOwnerTest)));
        SecurityContextHolder.setContext(securityContext);

        testCourseTitle.setTitle(testCourseTitle.getTitle().repeat(2));

        this.mockMvc.perform(get("/course/" + testCourseTitle.getTitle() + "/page/" + testCourse.getPage())
                        .param("courseTitle", testCourseTitle.getTitle())
                        .param("pageID", String.valueOf(testCourse.getPage())))
                .andDo(print())
                .andExpect(view().name(templateError404))
                .andExpect(status().isOk());
    }

    @Test
    @Sql(value = {"/clear-roles.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void GET_CoursePageAsCourseOwner_Failure_NoSuchRoleInDB() throws Exception {
        SecurityContextImpl securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(new RememberMeAuthenticationToken(
                "TestUser", testUser, AuthorityUtils.createAuthorityList(roleUser, roleCourseOwnerTest)));
        SecurityContextHolder.setContext(securityContext);

        this.mockMvc.perform(get("/course/" + testCourseTitle.getTitle() + "/page/" + testCourse.getPage())
                        .param("courseTitle", testCourseTitle.getTitle())
                        .param("pageID", String.valueOf(testCourse.getPage())))
                .andDo(print())
                .andExpect(view().name(templateErrorAccessDenied))
                .andExpect(status().isOk());
    }

    @Test
    @Sql(value = {"/clear-roles-from-users.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void GET_CoursePageAsCourseOwner_Failure_UserDoesNotHaveCorrespondingRole() throws Exception {
        SecurityContextImpl securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(new RememberMeAuthenticationToken(
                "TestUser", testUser, AuthorityUtils.createAuthorityList(roleUser)));
        SecurityContextHolder.setContext(securityContext);

        this.mockMvc.perform(get("/course/" + testCourseTitle.getTitle() + "/page/" + testCourse.getPage())
                        .param("courseTitle", testCourseTitle.getTitle())
                        .param("pageID", String.valueOf(testCourse.getPage())))
                .andDo(print())
                .andExpect(view().name(templateErrorAccessDenied))
                .andExpect(status().isOk());
    }

    @Test
    public void GET_CoursePageAsCourseOwner_Success() throws Exception {
        SecurityContextImpl securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(new RememberMeAuthenticationToken(
                "TestUser", testUser, AuthorityUtils.createAuthorityList(roleUser, roleCourseOwnerTest)));
        SecurityContextHolder.setContext(securityContext);

        this.mockMvc.perform(get("/course/" + testCourseTitle.getTitle() + "/page/" + testCourse.getPage())
                        .param("courseTitle", testCourseTitle.getTitle())
                        .param("pageID", String.valueOf(testCourse.getPage())))
                .andDo(print())
                .andExpect(view().name(templateCoursePage))
                .andExpect(model().attribute("lastVisitedPage", Matchers.any(Integer.class)))
                .andExpect(model().attribute("courseTitle", Matchers.any(CourseTitle.class)))
                .andExpect(model().attribute("course", Matchers.any(Course.class)))
                .andExpect(model().attribute("lastCoursePage", Matchers.any(Integer.class)))
                .andExpect(status().isOk());
    }

    @Test
    public void GET_CoursePageAsCourseOwner_Success_WithLastVisitedPageLessThenCourseGetPage() throws Exception {
        SecurityContextImpl securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(new RememberMeAuthenticationToken(
                "TestUser", testUser, AuthorityUtils.createAuthorityList(roleUser, roleCourseOwnerTest)));
        SecurityContextHolder.setContext(securityContext);

        String message = userCourseTooEarly;

        int page = testCourse.getPage() + 2;

        this.mockMvc.perform(get("/course/" + testCourseTitle.getTitle() + "/page/" + page)
                        .param("courseTitle", testCourseTitle.getTitle())
                        .param("pageID", String.valueOf(testCourse.getPage())))
                .andDo(print())
                .andExpect(view().name(templateCoursePage))
                .andExpect(model().attribute("lastCoursePage", Matchers.any(Integer.class)))
                .andExpect(model().attribute("tooEarly", Matchers.equalTo(message)))
                .andExpect(content().string(Matchers.containsString(message)))
                .andExpect(status().isOk());
    }

    @Test
    public void PATCH_UpdateCourseProgressAsCourseOwner_Failure_InvalidCourseTitle() throws Exception {
        SecurityContextImpl securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(new RememberMeAuthenticationToken(
                "TestUser", testUser, AuthorityUtils.createAuthorityList(roleUser, roleCourseOwnerTest)));
        SecurityContextHolder.setContext(securityContext);

        testCourseTitle.setTitle(testCourseTitle.getTitle().repeat(2));

        this.mockMvc.perform(patch("/course/" + testCourseTitle.getTitle() + "/page/" + testCourse.getPage())
                        .param("courseTitle", testCourseTitle.getTitle())
                        .param("pageID", String.valueOf(testCourse.getPage())))
                .andDo(print())
                .andExpect(redirectedUrl("/course/" + testCourseTitle.getTitle() + "/page/" + testCourse.getPage()))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    public void PATCH_UpdateCourseProgressAsCourseOwner_Failure_NoSuchCourseProgressInDB() throws Exception {
        SecurityContextImpl securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(new RememberMeAuthenticationToken(
                "TestUser", testUser, AuthorityUtils.createAuthorityList(roleUser, roleCourseOwnerTest)));
        SecurityContextHolder.setContext(securityContext);

        int page = testCourse.getPage() + 100;

        this.mockMvc.perform(patch("/course/" + testCourseTitle.getTitle() + "/page/" + page)
                        .param("courseTitle", testCourseTitle.getTitle())
                        .param("pageID", String.valueOf(testCourse.getPage())))
                .andDo(print())
                .andExpect(redirectedUrl("/course/" + testCourseTitle.getTitle() + "/page/" + page))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    public void PATCH_UpdateCourseProgressAsCourseOwner_Failure_NoNextCourseProgressInDB() throws Exception {
        SecurityContextImpl securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(new RememberMeAuthenticationToken(
                "TestUser", testUser, AuthorityUtils.createAuthorityList(roleUser, roleCourseOwnerTest)));
        SecurityContextHolder.setContext(securityContext);

        int page = 6;

        this.mockMvc.perform(patch("/course/" + testCourseTitle.getTitle() + "/page/" + page)
                        .param("courseTitle", testCourseTitle.getTitle())
                        .param("pageID", String.valueOf(testCourse.getPage())))
                .andDo(print())
                .andExpect(redirectedUrl("/course/" + testCourseTitle.getTitle() + "/page/" + page))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    public void PATCH_UpdateCourseProgressAsCourseOwner_Success() throws Exception {
        SecurityContextImpl securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(new RememberMeAuthenticationToken(
                "TestUser", testUser, AuthorityUtils.createAuthorityList(roleUser, roleCourseOwnerTest)));
        SecurityContextHolder.setContext(securityContext);

        int nextPage = testCourse.getPage() + 1;

        this.mockMvc.perform(patch("/course/" + testCourseTitle.getTitle() + "/page/" + testCourse.getPage())
                        .param("courseTitle", testCourseTitle.getTitle())
                        .param("pageID", String.valueOf(testCourse.getPage())))
                .andDo(print())
                .andExpect(redirectedUrl("/course/" + testCourseTitle.getTitle() + "/page/" + nextPage))
                .andExpect(status().is3xxRedirection());
    }
}