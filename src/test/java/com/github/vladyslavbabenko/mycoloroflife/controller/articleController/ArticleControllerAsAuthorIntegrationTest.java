package com.github.vladyslavbabenko.mycoloroflife.controller.articleController;

import com.github.vladyslavbabenko.mycoloroflife.AbstractTest.AbstractControllerIntegrationTest;
import com.github.vladyslavbabenko.mycoloroflife.entity.Article;
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
@DisplayName("Integration-level testing for ArticleController as Author")
@Sql(value = {"/create-test-values.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class ArticleControllerAsAuthorIntegrationTest extends AbstractControllerIntegrationTest {

    private User testAuthor;

    String symbols_101 = "IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII";

    //Templates
    @Value("${template.general.article.all}")
    String templateGeneralArticleAll;
    @Value("${template.author.article.edit}")
    String templateAuthorArticleEdit;
    @Value("${template.general.article}")
    String templateGeneralArticle;
    @Value("${template.author.article.add}")
    String templateAuthorArticleAdd;
    @Value("${template.error.access-denied}")
    String templateErrorAccessDenied;


    //Messages
    @Value("${article.exists.not}")
    String articleNotExists;
    @Value("${validation.text.length}")
    String validationTextLength;
    @Value("${validation.article.not.empty}")
    String validationArticleNotEmpty;
    @Value("${validation.title.not.empty}")
    String validationTitleNotEmpty;
    @Value("${validation.title.length}")
    String validationTitleLength;
    @Value("${article.exists.already}")
    String articleExistsAlready;
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
    public void isArticleControllerIntegrationTestSetUpForTests() {
        ServletContext servletContext = webApplicationContext.getServletContext();

        Assertions.assertThat(servletContext).isNotNull().isInstanceOf(MockServletContext.class);
        Assertions.assertThat(webApplicationContext.getBean("articleController")).isNotNull();
    }

    @Test
    public void GET_ArticlesPageAsAuthor() throws Exception {
        this.mockMvc.perform(get("/article")
                        .param("keyword", "First"))
                .andDo(print())
                .andExpect(view().name(templateGeneralArticleAll))
                .andExpect(model().attribute("listOfArticles", Matchers.any(List.class)))
                .andExpect(model().attribute("pageID", Matchers.any(Integer.class)))
                .andExpect(model().attribute("numberOfPages", Matchers.any(int[].class)))
                .andExpect(status().isOk());
    }

    @Test
    public void GET_ArticlesPageAsAuthor_WithKeyword() throws Exception {
        this.mockMvc.perform(get("/article"))
                .andDo(print())
                .andExpect(view().name(templateGeneralArticleAll))
                .andExpect(model().attribute("listOfArticles", Matchers.any(List.class)))
                .andExpect(model().attribute("pageID", Matchers.any(Integer.class)))
                .andExpect(model().attribute("numberOfPages", Matchers.any(int[].class)))
                .andExpect(status().isOk());
    }

    @Test
    public void GET_ArticlePageByIdAsAuthor() throws Exception {
        this.mockMvc.perform(get("/article/1"))
                .andDo(print())
                .andExpect(view().name(templateGeneralArticle))
                .andExpect(model().attribute("article", Matchers.any(Article.class)))
                .andExpect(status().isOk());
    }

    @Test
    public void GET_NewArticlePageAsAuthor() throws Exception {
        this.mockMvc.perform(get("/article/new"))
                .andDo(print())
                .andExpect(view().name(templateAuthorArticleAdd))
                .andExpect(model().attribute("article", Matchers.any(Article.class)))
                .andExpect(status().isOk());
    }

    @Test
    public void POST_CreateNewArticleAsAuthor_WithEmptyTitleError() throws Exception {
        String errorMessage = validationTitleNotEmpty;

        this.mockMvc.perform(post("/article")
                        .param("title", "")
                        .param("text", "First test text"))
                .andDo(print())
                .andExpect(view().name(templateAuthorArticleAdd))
                .andExpect(content().string(Matchers.containsString(errorMessage)))
                .andExpect(status().isOk());
    }

    @Test
    public void POST_CreateNewArticleAsAuthor_WithTitleOutOfBoundsError() throws Exception {
        String errorMessage = validationTitleLength;

        this.mockMvc.perform(post("/article")
                        .param("title", symbols_101)
                        .param("text", "First test text"))
                .andDo(print())
                .andExpect(view().name(templateAuthorArticleAdd))
                .andExpect(content().string(Matchers.containsString(errorMessage)))
                .andExpect(status().isOk());
    }

    @Test
    public void POST_CreateNewArticleAsAuthor_WithEmptyTextError() throws Exception {
        String errorMessage = validationArticleNotEmpty;

        this.mockMvc.perform(post("/article")
                        .param("title", "First test title")
                        .param("text", ""))
                .andDo(print())
                .andExpect(view().name(templateAuthorArticleAdd))
                .andExpect(content().string(Matchers.containsString(errorMessage)))
                .andExpect(status().isOk());
    }

    @Test
    public void POST_CreateNewArticleAsAuthor_WithTextOutOfBoundsError() throws Exception {
        String errorMessage = validationTextLength;

        StringBuilder longTestString = new StringBuilder();
        longTestString.setLength(65650);
        longTestString.append(symbols_101.repeat(650));

        this.mockMvc.perform(post("/article")
                        .param("title", "First text title")
                        .param("text", String.valueOf(longTestString)))
                .andDo(print())
                .andExpect(view().name(templateAuthorArticleAdd))
                .andExpect(content().string(Matchers.containsString(errorMessage)))
                .andExpect(status().isOk());
    }

    @Test
    public void POST_CreateNewArticleAsAuthor_WithSaveArticleFailure() throws Exception {
        SecurityContextImpl securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(new RememberMeAuthenticationToken(
                "TestAuthor", testAuthor, AuthorityUtils.createAuthorityList(roleUser, roleAuthor)));
        SecurityContextHolder.setContext(securityContext);

        String errorMessage = articleExistsAlready;

        this.mockMvc.perform(post("/article")
                        .param("title", "First test title")
                        .param("text", "First text text"))
                .andDo(print())
                .andExpect(view().name(templateAuthorArticleAdd))
                .andExpect(model().attribute("articleExistsAlready", Matchers.equalTo(errorMessage)))
                .andExpect(content().string(Matchers.containsString(errorMessage)))
                .andExpect(status().isOk());
    }

    @Test
    @Sql(value = {"/clear-articles.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void POST_CreateNewArticleAsAuthor_WithSaveArticleSuccess() throws Exception {
        SecurityContextImpl securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(new RememberMeAuthenticationToken(
                "TestAuthor", testAuthor, AuthorityUtils.createAuthorityList(roleUser, roleAuthor)));
        SecurityContextHolder.setContext(securityContext);

        this.mockMvc.perform(post("/article")
                        .param("title", "New test title as Author")
                        .param("text", "New text text"))
                .andDo(print())
                .andExpect(view().name(templateGeneralArticleAll))
                .andExpect(status().isOk());
    }

    @Test
    public void GET_ArticlePageByIdAsAuthor_With_ArticleNotFound() throws Exception {
        String errorMessage = articleNotExists;

        this.mockMvc.perform(get("/article/" + Integer.MAX_VALUE))
                .andDo(print())
                .andExpect(view().name(templateGeneralArticle))
                .andExpect(model().attribute("articleNotFound", Matchers.equalTo(errorMessage)))
                .andExpect(status().isOk());
    }

    @Test
    public void GET_EditArticlePageAsAuthor_WithArticleNotFound() throws Exception {
        SecurityContextImpl securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(new RememberMeAuthenticationToken(
                "TestAuthor", testAuthor, AuthorityUtils.createAuthorityList(roleUser, roleAuthor)));
        SecurityContextHolder.setContext(securityContext);

        String errorMessage = articleNotExists;

        this.mockMvc.perform(get("/article/" + Integer.MAX_VALUE + "/edit"))
                .andDo(print())
                .andExpect(model().attribute("articleNotFound", Matchers.equalTo(errorMessage)))
                .andExpect(view().name(templateGeneralArticle))
                .andExpect(status().isOk());
    }

    @Test
    public void DELETE_ArticleByIDAsAuthor() throws Exception {
        SecurityContextImpl securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(new RememberMeAuthenticationToken(
                "TestAuthor", testAuthor, AuthorityUtils.createAuthorityList(roleUser, roleAuthor)));
        SecurityContextHolder.setContext(securityContext);

        this.mockMvc.perform(delete("/article/1"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/article"));
    }

    @Test
    public void GET_EditArticlePageAsAuthor_WithArticleWrittenByAuthor() throws Exception {
        SecurityContextImpl securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(new RememberMeAuthenticationToken(
                "TestAuthor", testAuthor, AuthorityUtils.createAuthorityList(roleUser, roleAuthor)));
        SecurityContextHolder.setContext(securityContext);

        this.mockMvc.perform(get("/article/2/edit"))
                .andDo(print())
                .andExpect(view().name(templateAuthorArticleEdit))
                .andExpect(model().attribute("article", Matchers.any(Article.class)))
                .andExpect(status().isOk());
    }

    @Test
    public void GET_EditArticlePageAsAuthor_WithArticleWrittenByAdmin() throws Exception {
        SecurityContextImpl securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(new RememberMeAuthenticationToken(
                "TestAuthor", testAuthor, AuthorityUtils.createAuthorityList(roleUser, roleAuthor)));
        SecurityContextHolder.setContext(securityContext);

        this.mockMvc.perform(get("/article/1/edit"))
                .andDo(print())
                .andExpect(view().name(templateErrorAccessDenied))
                .andExpect(status().isOk());
    }

    @Test
    public void PUT_UpdateArticleAsAuthor_WithEmptyTitleError() throws Exception {
        String errorMessage = validationTitleNotEmpty;

        this.mockMvc.perform(put("/article")
                        .param("title", "")
                        .param("text", "First test text"))
                .andDo(print())
                .andExpect(view().name(templateAuthorArticleEdit))
                .andExpect(content().string(Matchers.containsString(errorMessage)))
                .andExpect(status().isOk());
    }

    @Test
    public void PUT_UpdateArticleAsAuthor_WithTitleOutOfBoundsError() throws Exception {
        String errorMessage = validationTitleLength;

        this.mockMvc.perform(put("/article")
                        .param("title", symbols_101)
                        .param("text", "First test text"))
                .andDo(print())
                .andExpect(view().name(templateAuthorArticleEdit))
                .andExpect(content().string(Matchers.containsString(errorMessage)))
                .andExpect(status().isOk());
    }

    @Test
    public void PUT_UpdateArticleAsAuthor_WithEmptyTextError() throws Exception {
        String errorMessage = validationArticleNotEmpty;

        this.mockMvc.perform(put("/article")
                        .param("title", "First test title")
                        .param("text", ""))
                .andDo(print())
                .andExpect(view().name(templateAuthorArticleEdit))
                .andExpect(content().string(Matchers.containsString(errorMessage)))
                .andExpect(status().isOk());
    }

    @Test
    public void PUT_UpdateArticleAsAuthor_WithTextOutOfBoundsError() throws Exception {
        String errorMessage = validationTextLength;

        StringBuilder longTestString = new StringBuilder();
        longTestString.setLength(65650);
        longTestString.append(symbols_101.repeat(650));

        this.mockMvc.perform(put("/article")
                        .param("title", "First text title")
                        .param("text", String.valueOf(longTestString)))
                .andDo(print())
                .andExpect(view().name(templateAuthorArticleEdit))
                .andExpect(content().string(Matchers.containsString(errorMessage)))
                .andExpect(status().isOk());
    }

    @Test
    public void PUT_UpdateArticleAsAuthor_WithSaveArticleFailure() throws Exception {
        SecurityContextImpl securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(new RememberMeAuthenticationToken(
                "TestAuthor", testAuthor, AuthorityUtils.createAuthorityList(roleUser, roleAuthor)));
        SecurityContextHolder.setContext(securityContext);

        String errorMessage = articleNotExists;

        this.mockMvc.perform(put("/article")
                        .param("id", "-1")
                        .param("title", "First test title")
                        .param("text", "First text text"))
                .andDo(print())
                .andExpect(view().name(templateAuthorArticleEdit))
                .andExpect(model().attribute("articleNotFound", Matchers.equalTo(errorMessage)))
                .andExpect(content().string(Matchers.containsString(errorMessage)))
                .andExpect(status().isOk());
    }

    @Test
    public void PUT_UpdateArticleAsAuthor_WithSaveArticleSuccess() throws Exception {
        SecurityContextImpl securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(new RememberMeAuthenticationToken(
                "TestAuthor", testAuthor, AuthorityUtils.createAuthorityList(roleUser, roleAuthor)));
        SecurityContextHolder.setContext(securityContext);

        this.mockMvc.perform(put("/article")
                        .param("id", "2")
                        .param("title", "First test title")
                        .param("text", "First text text"))
                .andDo(print())
                .andExpect(view().name(templateGeneralArticleAll))
                .andExpect(status().isOk());
    }
}