package com.github.vladyslavbabenko.mycoloroflife.controller.eventController;

import com.github.vladyslavbabenko.mycoloroflife.AbstractTest.AbstractControllerIntegrationTest;
import com.github.vladyslavbabenko.mycoloroflife.entity.Event;
import com.github.vladyslavbabenko.mycoloroflife.entity.User;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WithMockUser(username = "TestAuthor", roles = {"USER", "AUTHOR"})
@DisplayName("Integration-level testing for EventController as Author")
@Sql(value = {"/create-test-values.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class EventControllerAsAuthorIntegrationTest extends AbstractControllerIntegrationTest {

    private User testAuthor;

    String symbols_101 = "IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII";

    //Templates
    @Value("${template.general.event.all}")
    String templateGeneralEventAll;

    @Value("${template.general.event}")
    String templateGeneralEvent;

    @Value("${template.author.event.add}")
    String templateAuthorEventAdd;

    @Value("${template.author.event.edit}")
    String templateAuthorEventEdit;

    @Value("${template.error.access-denied}")
    String templateErrorAccessDenied;

    //Messages
    @Value("${event.exists.not}")
    String eventNotExists;

    @Value("${validation.title.not.empty}")
    String validationTitleNotEmpty;

    @Value("${validation.title.length}")
    String validationTitleLength;

    @Value("${validation.description.not.empty}")
    String validationDescriptionNotEmpty;

    @Value("${validation.text.length}")
    String validationTextLength;

    @Value("${event.exists.already}")
    String eventExistsAlready;

    @Value("${role.user}")
    String roleUser;

    @Value("${role.author}")
    String roleAuthor;

    @BeforeEach
    void setUp() {
        super.setup();

        testAuthor = User.builder()
                .id(3)
                .name("TestAuthor")
                .email("TestAuthor@mail.com")
                .build();
    }

    @Test
    public void isEventControllerIntegrationTestSetUpForTests() {
        ServletContext servletContext = webApplicationContext.getServletContext();

        Assertions.assertThat(servletContext).isNotNull().isInstanceOf(MockServletContext.class);
        Assertions.assertThat(webApplicationContext.getBean("eventController")).isNotNull();
    }

    @Test
    public void GET_EventsPageAsAuthor_WithKeyword() throws Exception {
        this.mockMvc.perform(get("/event")
                        .param("keyword", "First"))
                .andDo(print())
                .andExpect(view().name(templateGeneralEventAll))
                .andExpect(model().attribute("listOfEvents", Matchers.any(List.class)))
                .andExpect(model().attribute("pageID", Matchers.any(Integer.class)))
                .andExpect(model().attribute("numberOfPages", Matchers.any(int[].class)))
                .andExpect(status().isOk());
    }

    @Test
    public void GET_EventsPageAsAuthor() throws Exception {
        this.mockMvc.perform(get("/event"))
                .andDo(print())
                .andExpect(view().name(templateGeneralEventAll))
                .andExpect(model().attribute("listOfEvents", Matchers.any(List.class)))
                .andExpect(model().attribute("pageID", Matchers.any(Integer.class)))
                .andExpect(model().attribute("numberOfPages", Matchers.any(int[].class)))
                .andExpect(status().isOk());
    }

    @Test
    public void GET_EventPageByIdAsAuthor() throws Exception {
        this.mockMvc.perform(get("/event/1"))
                .andDo(print())
                .andExpect(view().name(templateGeneralEvent))
                .andExpect(model().attribute("event", Matchers.any(Event.class)))
                .andExpect(status().isOk());
    }

    @Test
    public void GET_NewEventPageAsAuthor() throws Exception {
        this.mockMvc.perform(get("/event/new"))
                .andDo(print())
                .andExpect(view().name(templateAuthorEventAdd))
                .andExpect(model().attribute("event", Matchers.any(Event.class)))
                .andExpect(status().isOk());
    }

    @Test
    public void POST_CreateNewEventAsAuthor_WithEmptyTitleError() throws Exception {
        String errorMessage = validationTitleNotEmpty;

        this.mockMvc.perform(post("/event")
                        .param("title", "")
                        .param("text", "First test text"))
                .andDo(print())
                .andExpect(view().name(templateAuthorEventAdd))
                .andExpect(content().string(Matchers.containsString(errorMessage)))
                .andExpect(status().isOk());
    }

    @Test
    public void POST_CreateNewEventAsAuthor_WithTitleOutOfBoundsError() throws Exception {
        String errorMessage = validationTitleLength;

        this.mockMvc.perform(post("/event")
                        .param("title", symbols_101)
                        .param("text", "First test text"))
                .andDo(print())
                .andExpect(view().name(templateAuthorEventAdd))
                .andExpect(content().string(Matchers.containsString(errorMessage)))
                .andExpect(status().isOk());
    }

    @Test
    public void POST_CreateNewEventAsAuthor_WithEmptyTextError() throws Exception {
        String errorMessage = validationDescriptionNotEmpty;

        this.mockMvc.perform(post("/event")
                        .param("title", "First test title")
                        .param("text", ""))
                .andDo(print())
                .andExpect(view().name(templateAuthorEventAdd))
                .andExpect(content().string(Matchers.containsString(errorMessage)))
                .andExpect(status().isOk());
    }

    @Test
    public void POST_CreateNewEventAsAuthor_WithTextOutOfBoundsError() throws Exception {
        String errorMessage = validationTextLength;

        StringBuilder longTestString = new StringBuilder();
        longTestString.setLength(65650);
        longTestString.append(symbols_101.repeat(650));

        this.mockMvc.perform(post("/event")
                        .param("title", "First text title")
                        .param("text", String.valueOf(longTestString)))
                .andDo(print())
                .andExpect(view().name(templateAuthorEventAdd))
                .andExpect(content().string(Matchers.containsString(errorMessage)))
                .andExpect(status().isOk());
    }

    @Test
    public void POST_CreateNewEventAsAuthor_WithSaveEventFailure() throws Exception {
        SecurityContextImpl securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(new RememberMeAuthenticationToken(
                "TestAuthor", testAuthor, AuthorityUtils.createAuthorityList(roleUser, roleAuthor)));
        SecurityContextHolder.setContext(securityContext);

        String errorMessage = eventExistsAlready;

        this.mockMvc.perform(post("/event")
                        .param("title", "First test title")
                        .param("text", "First text text"))
                .andDo(print())
                .andExpect(view().name(templateAuthorEventAdd))
                .andExpect(model().attribute("eventExistsAlready", Matchers.equalTo(errorMessage)))
                .andExpect(content().string(Matchers.containsString(errorMessage)))
                .andExpect(status().isOk());
    }

    @Test
    @Sql(value = {"/clear-events.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void POST_CreateNewEventAsAuthor_WithSaveEventSuccess() throws Exception {
        SecurityContextImpl securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(new RememberMeAuthenticationToken(
                "TestAuthor", testAuthor, AuthorityUtils.createAuthorityList(roleUser, roleAuthor)));
        SecurityContextHolder.setContext(securityContext);

        this.mockMvc.perform(post("/event")
                        .param("title", "New test title as Author")
                        .param("text", "New text text"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/event"));
    }

    @Test
    public void DELETE_EventByIDAsAuthor() throws Exception {
        SecurityContextImpl securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(new RememberMeAuthenticationToken(
                "TestAuthor", testAuthor, AuthorityUtils.createAuthorityList(roleUser, roleAuthor)));
        SecurityContextHolder.setContext(securityContext);

        this.mockMvc.perform(delete("/event/1"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/event"));
    }

    @Test
    public void GET_EditEventPageAsAuthor_WithEventWrittenByAuthor() throws Exception {
        SecurityContextImpl securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(new RememberMeAuthenticationToken(
                "TestAuthor", testAuthor, AuthorityUtils.createAuthorityList(roleUser, roleAuthor)));
        SecurityContextHolder.setContext(securityContext);

        this.mockMvc.perform(get("/event/2/edit"))
                .andDo(print())
                .andExpect(view().name(templateAuthorEventEdit))
                .andExpect(model().attribute("event", Matchers.any(Event.class)))
                .andExpect(status().isOk());
    }

    @Test
    public void GET_EditEventPageAsAuthor_WithEventWrittenByAdmin() throws Exception {
        SecurityContextImpl securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(new RememberMeAuthenticationToken(
                "TestAuthor", testAuthor, AuthorityUtils.createAuthorityList(roleUser, roleAuthor)));
        SecurityContextHolder.setContext(securityContext);

        this.mockMvc.perform(get("/event/1/edit"))
                .andDo(print())
                .andExpect(view().name(templateErrorAccessDenied))
                .andExpect(status().isOk());
    }

    @Test
    public void PUT_UpdateEventAsAuthor_WithEmptyTitleError() throws Exception {
        String errorMessage = validationTitleNotEmpty;

        this.mockMvc.perform(put("/event")
                        .param("title", "")
                        .param("text", "First test text"))
                .andDo(print())
                .andExpect(view().name(templateAuthorEventEdit))
                .andExpect(content().string(Matchers.containsString(errorMessage)))
                .andExpect(status().isOk());
    }

    @Test
    public void PUT_UpdateEventAsAuthor_WithTitleOutOfBoundsError() throws Exception {
        String errorMessage = validationTitleLength;
        this.mockMvc.perform(put("/event")
                        .param("title", symbols_101)
                        .param("text", "First test text"))
                .andDo(print())
                .andExpect(view().name(templateAuthorEventEdit))
                .andExpect(content().string(Matchers.containsString(errorMessage)))
                .andExpect(status().isOk());
    }

    @Test
    public void PUT_UpdateEventAsAuthor_WithEmptyTextError() throws Exception {
        String errorMessage = validationDescriptionNotEmpty;

        this.mockMvc.perform(put("/event")
                        .param("title", "First test title")
                        .param("text", ""))
                .andDo(print())
                .andExpect(view().name(templateAuthorEventEdit))
                .andExpect(content().string(Matchers.containsString(errorMessage)))
                .andExpect(status().isOk());
    }

    @Test
    public void PUT_UpdateEventAsAuthor_WithTextOutOfBoundsError() throws Exception {
        String errorMessage = validationTextLength;

        StringBuilder longTestString = new StringBuilder();
        longTestString.setLength(65650);
        longTestString.append(symbols_101.repeat(650));

        this.mockMvc.perform(put("/event")
                        .param("title", "First text title")
                        .param("text", String.valueOf(longTestString)))
                .andDo(print())
                .andExpect(view().name(templateAuthorEventEdit))
                .andExpect(content().string(Matchers.containsString(errorMessage)))
                .andExpect(status().isOk());
    }

    @Test
    public void GET_EventPageByIdAsAuthor_With_EventNotFound() throws Exception {
        String errorMessage = eventNotExists;

        this.mockMvc.perform(get("/event/" + Integer.MAX_VALUE))
                .andDo(print())
                .andExpect(view().name(templateGeneralEvent))
                .andExpect(model().attribute("eventNotFound", Matchers.equalTo(errorMessage)))
                .andExpect(status().isOk());
    }

    @Test
    public void GET_EditEventPageAsAuthor_WithEventNotFound() throws Exception {
        SecurityContextImpl securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(new RememberMeAuthenticationToken(
                "testAuthor", testAuthor, AuthorityUtils.createAuthorityList(roleUser, roleAuthor)));
        SecurityContextHolder.setContext(securityContext);

        String errorMessage = eventNotExists;

        this.mockMvc.perform(get("/event/" + Integer.MAX_VALUE + "/edit"))
                .andDo(print())
                .andExpect(model().attribute("eventNotFound", Matchers.equalTo(errorMessage)))
                .andExpect(view().name(templateGeneralEvent))
                .andExpect(status().isOk());
    }

    @Test
    public void PUT_UpdateEventAsAuthor_WithSaveEventFailure() throws Exception {
        SecurityContextImpl securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(new RememberMeAuthenticationToken(
                "TestAuthor", testAuthor, AuthorityUtils.createAuthorityList(roleUser, roleAuthor)));
        SecurityContextHolder.setContext(securityContext);

        String errorMessage = eventNotExists;

        this.mockMvc.perform(put("/event")
                        .param("id", "-1")
                        .param("title", "First test title")
                        .param("text", "First text text"))
                .andDo(print())
                .andExpect(view().name(templateAuthorEventEdit))
                .andExpect(model().attribute("eventNotFound", Matchers.equalTo(errorMessage)))
                .andExpect(content().string(Matchers.containsString(errorMessage)))
                .andExpect(status().isOk());
    }

    @Test
    public void PUT_UpdateEventAsAuthor_WithSaveEventSuccess() throws Exception {
        SecurityContextImpl securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(new RememberMeAuthenticationToken(
                "TestAuthor", testAuthor, AuthorityUtils.createAuthorityList(roleUser, roleAuthor)));
        SecurityContextHolder.setContext(securityContext);

        this.mockMvc.perform(put("/event")
                        .param("id", "2")
                        .param("title", "First test title")
                        .param("text", "First text text"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/event"));
    }
}