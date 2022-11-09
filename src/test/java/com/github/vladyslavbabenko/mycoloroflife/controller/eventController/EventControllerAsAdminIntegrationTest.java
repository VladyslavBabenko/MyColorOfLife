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

@WithMockUser(username = "TestAdmin", roles = {"USER", "ADMIN"})
@DisplayName("Integration-level testing for EventController as Admin")
@Sql(value = {"/create-test-values.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class EventControllerAsAdminIntegrationTest extends AbstractControllerIntegrationTest {

    private User testAdmin;

    String symbols_101 = "IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII";

    //Templates
    @Value("${template.general.event.all}")
    String templateGeneralEventAll; //generalTemplate/eventsPage

    @Value("${template.general.event}")
    String templateGeneralEvent; //generalTemplate/eventPage

    @Value("${template.author.event.add}")
    String templateAuthorEventAdd; //authorTemplate/newEventPage

    @Value("${template.author.event.edit}")
    String templateAuthorEventEdit; //authorTemplate/editEventPage


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

    @Value("${role.admin}")
    String roleAdmin;

    @BeforeEach
    void setUp() {
        super.setup();

        testAdmin = User.builder()
                .id(2)
                .name("TestAdmin")
                .email("TestAdmin@mail.com")
                .build();
    }

    @Test
    public void isEventControllerIntegrationTestSetUpForTests() {
        ServletContext servletContext = webApplicationContext.getServletContext();

        Assertions.assertThat(servletContext).isNotNull().isInstanceOf(MockServletContext.class);
        Assertions.assertThat(webApplicationContext.getBean("eventController")).isNotNull();
    }

    @Test
    public void GET_EventsPageAsAdmin() throws Exception {
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
    public void GET_EventsPageAsAdmin_WithKeyword() throws Exception {
        this.mockMvc.perform(get("/event"))
                .andDo(print())
                .andExpect(view().name(templateGeneralEventAll))
                .andExpect(model().attribute("listOfEvents", Matchers.any(List.class)))
                .andExpect(model().attribute("pageID", Matchers.any(Integer.class)))
                .andExpect(model().attribute("numberOfPages", Matchers.any(int[].class)))
                .andExpect(status().isOk());
    }

    @Test
    public void GET_EventPageByIdAsAdmin() throws Exception {
        this.mockMvc.perform(get("/event/1"))
                .andDo(print())
                .andExpect(view().name(templateGeneralEvent))
                .andExpect(model().attribute("event", Matchers.any(Event.class)))
                .andExpect(status().isOk());
    }

    @Test
    public void GET_EventPageByIdAsAdmin_With_EventNotFound() throws Exception {
        String errorMessage = eventNotExists;

        this.mockMvc.perform(get("/event/" + Integer.MAX_VALUE))
                .andDo(print())
                .andExpect(view().name(templateGeneralEvent))
                .andExpect(model().attribute("eventNotFound", Matchers.equalTo(errorMessage)))
                .andExpect(status().isOk());
    }

    @Test
    public void GET_EditEventPageAsAdmin_WithEventNotFound() throws Exception {
        SecurityContextImpl securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(new RememberMeAuthenticationToken(
                "TestAdmin", testAdmin, AuthorityUtils.createAuthorityList(roleUser, roleAdmin)));
        SecurityContextHolder.setContext(securityContext);

        String errorMessage = eventNotExists;

        this.mockMvc.perform(get("/event/" + Integer.MAX_VALUE + "/edit"))
                .andDo(print())
                .andExpect(model().attribute("eventNotFound", Matchers.equalTo(errorMessage)))
                .andExpect(view().name(templateGeneralEvent))
                .andExpect(status().isOk());
    }

    @Test
    public void GET_NewEventPageAsAdmin() throws Exception {
        this.mockMvc.perform(get("/event/new"))
                .andDo(print())
                .andExpect(view().name(templateAuthorEventAdd))
                .andExpect(model().attribute("event", Matchers.any(Event.class)))
                .andExpect(status().isOk());
    }

    @Test
    public void POST_CreateNewEventAsAdmin_WithEmptyTitleError() throws Exception {
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
    public void POST_CreateNewEventAsAdmin_WithTitleOutOfBoundsError() throws Exception {
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
    public void POST_CreateNewEventAsAdmin_WithEmptyTextError() throws Exception {
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
    public void POST_CreateNewEventAsAdmin_WithTextOutOfBoundsError() throws Exception {
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
    public void POST_CreateNewEventAsAdmin_WithSaveEventFailure() throws Exception {
        SecurityContextImpl securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(new RememberMeAuthenticationToken(
                "TestAdmin", testAdmin, AuthorityUtils.createAuthorityList(roleUser, roleAdmin)));
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
    public void POST_CreateNewEventAsAdmin_WithSaveEventSuccess() throws Exception {
        SecurityContextImpl securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(new RememberMeAuthenticationToken(
                "TestAdmin", testAdmin, AuthorityUtils.createAuthorityList(roleUser, roleAdmin)));
        SecurityContextHolder.setContext(securityContext);

        this.mockMvc.perform(post("/event")
                        .param("title", "New test title As Admin")
                        .param("text", "New text text 2"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/event"));
    }

    @Test
    public void DELETE_EventByIDAsAdmin() throws Exception {
        SecurityContextImpl securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(new RememberMeAuthenticationToken(
                "TestAdmin", testAdmin, AuthorityUtils.createAuthorityList(roleUser, roleAdmin)));
        SecurityContextHolder.setContext(securityContext);

        this.mockMvc.perform(delete("/event/1"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/event"));
    }

    @Test
    public void GET_EditEventPageAsAdmin_WithEventWrittenByAuthor() throws Exception {
        SecurityContextImpl securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(new RememberMeAuthenticationToken(
                "TestAdmin", testAdmin, AuthorityUtils.createAuthorityList(roleUser, roleAdmin)));
        SecurityContextHolder.setContext(securityContext);

        this.mockMvc.perform(get("/event/2/edit"))
                .andDo(print())
                .andExpect(view().name(templateAuthorEventEdit))
                .andExpect(model().attribute("event", Matchers.any(Event.class)))
                .andExpect(status().isOk());
    }

    @Test
    public void GET_EditEventPageAsAdmin_WithEventWrittenByAdmin() throws Exception {
        SecurityContextImpl securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(new RememberMeAuthenticationToken(
                "TestAdmin", testAdmin, AuthorityUtils.createAuthorityList(roleUser, roleAdmin)));
        SecurityContextHolder.setContext(securityContext);

        this.mockMvc.perform(get("/event/1/edit"))
                .andDo(print())
                .andExpect(view().name(templateAuthorEventEdit))
                .andExpect(status().isOk());
    }

    @Test
    public void PUT_UpdateEventAsAdmin_WithEmptyTitleError() throws Exception {
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
    public void PUT_UpdateEventAsAdmin_WithTitleOutOfBoundsError() throws Exception {
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
    public void PUT_UpdateEventAsAdmin_WithEmptyTextError() throws Exception {
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
    public void PUT_UpdateEventAsAdmin_WithTextOutOfBoundsError() throws Exception {
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
    public void PUT_UpdateEventAsAdmin_WithSaveEventFailure() throws Exception {
        SecurityContextImpl securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(new RememberMeAuthenticationToken(
                "TestAdmin", testAdmin, AuthorityUtils.createAuthorityList(roleUser, roleAdmin)));
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
    public void PUT_UpdateEventAsAdmin_WithSaveEventSuccess() throws Exception {
        SecurityContextImpl securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(new RememberMeAuthenticationToken(
                "TestAdmin", testAdmin, AuthorityUtils.createAuthorityList(roleUser, roleAdmin)));
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