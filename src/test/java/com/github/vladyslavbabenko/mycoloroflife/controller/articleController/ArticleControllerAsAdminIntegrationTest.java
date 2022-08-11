package com.github.vladyslavbabenko.mycoloroflife.controller.articleController;

import com.github.vladyslavbabenko.mycoloroflife.controller.AbstractControllerIntegrationTest;
import com.github.vladyslavbabenko.mycoloroflife.entity.Article;
import com.github.vladyslavbabenko.mycoloroflife.entity.User;
import org.fest.assertions.api.Assertions;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
@DisplayName("Integration-level testing for ArticleController as Admin")
@Sql(value = {"/create-test-values.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class ArticleControllerAsAdminIntegrationTest extends AbstractControllerIntegrationTest {

    private User testAdmin;

    @BeforeEach
    void setUp() {
        super.setup();

        testAdmin = User.builder()
                .id(2)
                .username("TestAdmin")
                .email("TestAdmin@mail.com")
                .build();
    }

    @Test
    public void isArticleControllerIntegrationTestSetUpForTests() {
        ServletContext servletContext = webApplicationContext.getServletContext();

        Assertions.assertThat(servletContext).isNotNull().isInstanceOf(MockServletContext.class);
        Assertions.assertThat(webApplicationContext.getBean("articleController")).isNotNull();
    }

    @Test
    public void GET_ArticlesPageAsAdmin() throws Exception {
        this.mockMvc.perform(get("/article")
                        .param("keyword", "First"))
                .andDo(print())
                .andExpect(view().name("generalTemplate/articlesPage"))
                .andExpect(model().attribute("listOfArticles", Matchers.any(List.class)))
                .andExpect(model().attribute("pageID", Matchers.any(Integer.class)))
                .andExpect(model().attribute("numberOfPages", Matchers.any(int[].class)))
                .andExpect(status().isOk());
    }

    @Test
    public void GET_ArticlesPageAsAdmin_WithKeyword() throws Exception {
        this.mockMvc.perform(get("/article"))
                .andDo(print())
                .andExpect(view().name("generalTemplate/articlesPage"))
                .andExpect(model().attribute("listOfArticles", Matchers.any(List.class)))
                .andExpect(model().attribute("pageID", Matchers.any(Integer.class)))
                .andExpect(model().attribute("numberOfPages", Matchers.any(int[].class)))
                .andExpect(status().isOk());
    }

    @Test
    public void GET_ArticlePageByIdAsAdmin() throws Exception {
        this.mockMvc.perform(get("/article/1"))
                .andDo(print())
                .andExpect(view().name("generalTemplate/articlePage"))
                .andExpect(model().attribute("article", Matchers.any(Article.class)))
                .andExpect(status().isOk());
    }

    @Test
    public void GET_NewArticlePageAsAdmin() throws Exception {
        this.mockMvc.perform(get("/article/new"))
                .andDo(print())
                .andExpect(view().name("authorTemplate/newArticlePage"))
                .andExpect(model().attribute("article", Matchers.any(Article.class)))
                .andExpect(status().isOk());
    }

    @Test
    public void POST_CreateNewArticleAsAdmin_WithEmptyTitleError() throws Exception {
        String errorMessage = "Назва не повинна бути порожньою";

        this.mockMvc.perform(post("/article")
                        .param("title", "")
                        .param("text", "First test text"))
                .andDo(print())
                .andExpect(view().name("authorTemplate/newArticlePage"))
                .andExpect(content().string(Matchers.containsString(errorMessage)))
                .andExpect(status().isOk());
    }

    @Test
    public void POST_CreateNewArticleAsAdmin_WithTitleOutOfBoundsError() throws Exception {
        String errorMessage = "Назва має бути від 1 до 100 символів";
        String symbols_101 = "IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII";

        this.mockMvc.perform(post("/article")
                        .param("title", symbols_101)
                        .param("text", "First test text"))
                .andDo(print())
                .andExpect(view().name("authorTemplate/newArticlePage"))
                .andExpect(content().string(Matchers.containsString(errorMessage)))
                .andExpect(status().isOk());
    }

    @Test
    public void POST_CreateNewArticleAsAdmin_WithEmptyTextError() throws Exception {
        String errorMessage = "Стаття не повинна бути порожньою";

        this.mockMvc.perform(post("/article")
                        .param("title", "First test title")
                        .param("text", ""))
                .andDo(print())
                .andExpect(view().name("authorTemplate/newArticlePage"))
                .andExpect(content().string(Matchers.containsString(errorMessage)))
                .andExpect(status().isOk());
    }

    @Test
    public void POST_CreateNewArticleAsAdmin_WithTextOutOfBoundsError() throws Exception {
        String errorMessage = "Стаття має бути від 1 до 65535 символів";

        String symbols_101 = "IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII";
        StringBuilder longTestString = new StringBuilder();
        longTestString.setLength(65650);
        longTestString.append(symbols_101.repeat(650));

        this.mockMvc.perform(post("/article")
                        .param("title", "First text title")
                        .param("text", String.valueOf(longTestString)))
                .andDo(print())
                .andExpect(view().name("authorTemplate/newArticlePage"))
                .andExpect(content().string(Matchers.containsString(errorMessage)))
                .andExpect(status().isOk());
    }

    @Test
    public void POST_CreateNewArticleAsAdmin_WithSaveArticleFailure() throws Exception {
        SecurityContextImpl securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(new RememberMeAuthenticationToken(
                "testAdmin", testAdmin, AuthorityUtils.createAuthorityList("ROLE_USER", "ROLE_ADMIN")));
        SecurityContextHolder.setContext(securityContext);

        String errorMessage = "Така стаття вже існує";

        this.mockMvc.perform(post("/article")
                        .param("title", "First test title")
                        .param("text", "First text text"))
                .andDo(print())
                .andExpect(view().name("authorTemplate/newArticlePage"))
                .andExpect(model().attribute("articleError", Matchers.equalTo(errorMessage)))
                .andExpect(content().string(Matchers.containsString(errorMessage)))
                .andExpect(status().isOk());
    }

    @Test
    @Sql(value = {"/clear-articles.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void POST_CreateNewArticleAsAdmin_WithSaveArticleSuccess() throws Exception {
        SecurityContextImpl securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(new RememberMeAuthenticationToken(
                "testAdmin", testAdmin, AuthorityUtils.createAuthorityList("ROLE_USER", "ROLE_ADMIN")));
        SecurityContextHolder.setContext(securityContext);

        this.mockMvc.perform(post("/article")
                        .param("title", "New test title As Admin")
                        .param("text", "New text text 2"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/article"));
    }

    @Test
    public void DELETE_ArticleByIDAsAdmin() throws Exception {
        SecurityContextImpl securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(new RememberMeAuthenticationToken(
                "testAdmin", testAdmin, AuthorityUtils.createAuthorityList("ROLE_USER", "ROLE_ADMIN")));
        SecurityContextHolder.setContext(securityContext);

        this.mockMvc.perform(delete("/article/1"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/article"));
    }

    @Test
    public void GET_EditArticlePageAsAdmin_WithArticleWrittenByAuthor() throws Exception {
        SecurityContextImpl securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(new RememberMeAuthenticationToken(
                "testAdmin", testAdmin, AuthorityUtils.createAuthorityList("ROLE_USER", "ROLE_ADMIN")));
        SecurityContextHolder.setContext(securityContext);

        this.mockMvc.perform(get("/article/2/edit"))
                .andDo(print())
                .andExpect(view().name("authorTemplate/editArticlePage"))
                .andExpect(model().attribute("article", Matchers.any(Article.class)))
                .andExpect(status().isOk());
    }

    @Test
    public void GET_EditArticlePageAsAdmin_WithArticleWrittenByAdmin() throws Exception {
        SecurityContextImpl securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(new RememberMeAuthenticationToken(
                "testAdmin", testAdmin, AuthorityUtils.createAuthorityList("ROLE_USER", "ROLE_ADMIN")));
        SecurityContextHolder.setContext(securityContext);

        this.mockMvc.perform(get("/article/1/edit"))
                .andDo(print())
                .andExpect(view().name("authorTemplate/editArticlePage"))
                .andExpect(status().isOk());
    }

    @Test
    public void PUT_UpdateArticleAsAdmin_WithEmptyTitleError() throws Exception {
        String errorMessage = "Назва не повинна бути порожньою";

        this.mockMvc.perform(put("/article")
                        .param("title", "")
                        .param("text", "First test text"))
                .andDo(print())
                .andExpect(view().name("authorTemplate/editArticlePage"))
                .andExpect(content().string(Matchers.containsString(errorMessage)))
                .andExpect(status().isOk());
    }

    @Test
    public void PUT_UpdateArticleAsAdmin_WithTitleOutOfBoundsError() throws Exception {
        String errorMessage = "Назва має бути від 1 до 100 символів";
        String symbols_101 = "IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII";

        this.mockMvc.perform(put("/article")
                        .param("title", symbols_101)
                        .param("text", "First test text"))
                .andDo(print())
                .andExpect(view().name("authorTemplate/editArticlePage"))
                .andExpect(content().string(Matchers.containsString(errorMessage)))
                .andExpect(status().isOk());
    }

    @Test
    public void PUT_UpdateArticleAsAdmin_WithEmptyTextError() throws Exception {
        String errorMessage = "Стаття не повинна бути порожньою";

        this.mockMvc.perform(put("/article")
                        .param("title", "First test title")
                        .param("text", ""))
                .andDo(print())
                .andExpect(view().name("authorTemplate/editArticlePage"))
                .andExpect(content().string(Matchers.containsString(errorMessage)))
                .andExpect(status().isOk());
    }

    @Test
    public void PUT_UpdateArticleAsAdmin_WithTextOutOfBoundsError() throws Exception {
        String errorMessage = "Стаття має бути від 1 до 65535 символів";

        String symbols_101 = "IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII";
        StringBuilder longTestString = new StringBuilder();
        longTestString.setLength(65650);
        longTestString.append(symbols_101.repeat(650));

        this.mockMvc.perform(put("/article")
                        .param("title", "First text title")
                        .param("text", String.valueOf(longTestString)))
                .andDo(print())
                .andExpect(view().name("authorTemplate/editArticlePage"))
                .andExpect(content().string(Matchers.containsString(errorMessage)))
                .andExpect(status().isOk());
    }

    @Test
    public void PUT_UpdateArticleAsAdmin_WithSaveArticleFailure() throws Exception {
        SecurityContextImpl securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(new RememberMeAuthenticationToken(
                "testAdmin", testAdmin, AuthorityUtils.createAuthorityList("ROLE_USER", "ROLE_ADMIN")));
        SecurityContextHolder.setContext(securityContext);

        String errorMessage = "Такої статті не існує";

        this.mockMvc.perform(put("/article")
                        .param("id", "-1")
                        .param("title", "First test title")
                        .param("text", "First text text"))
                .andDo(print())
                .andExpect(view().name("authorTemplate/editArticlePage"))
                .andExpect(model().attribute("updateArticleError", Matchers.equalTo(errorMessage)))
                .andExpect(content().string(Matchers.containsString(errorMessage)))
                .andExpect(status().isOk());
    }

    @Test
    public void PUT_UpdateArticleAsAdmin_WithSaveArticleSuccess() throws Exception {
        SecurityContextImpl securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(new RememberMeAuthenticationToken(
                "testAdmin", testAdmin, AuthorityUtils.createAuthorityList("ROLE_USER", "ROLE_ADMIN")));
        SecurityContextHolder.setContext(securityContext);

        this.mockMvc.perform(put("/article")
                        .param("id", "2")
                        .param("title", "First test title")
                        .param("text", "First text text"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/article"));
    }

}