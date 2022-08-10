package com.github.vladyslavbabenko.mycoloroflife.repository;

import com.github.vladyslavbabenko.mycoloroflife.entity.Article;
import com.github.vladyslavbabenko.mycoloroflife.entity.User;
import org.fest.assertions.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@DisplayName("Integration-level testing for ArticleRepository")
@Sql(value = {"/create-articles-and-users.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class ArticleRepositoryIntegrationTest extends AbstractRepositoryIntegrationTest {

    private final User testAdmin = User.builder().id(2).username("TestAdmin").email("TestAdmin@mail.com").build(),
            testAuthor = User.builder().id(3).username("TestAuthor").email("TestAuthor@mail.com").build();
    private Article expectedFirstArticle, expectedSecondArticle;
    @Autowired
    private ArticleRepository articleRepository;

    @BeforeEach
    void setUp() {
        //given
        expectedFirstArticle = Article.builder()
                .id(1)
                .dateTimeOfCreation("2022.08.05 22:00")
                .title("First test title")
                .text("First test text. First test text. First test text.")
                .users(Collections.singleton(testAdmin))
                .build();

        expectedSecondArticle = Article.builder()
                .id(2)
                .dateTimeOfCreation("2022.08.05 22:05")
                .title("Second test title")
                .text("Second test text. Second test text. Second test text.")
                .users(Collections.singleton(testAuthor))
                .build();
    }

    @Test
    void shouldFindArticleByTitle() {
        Optional<Article> actualArticle = articleRepository.findArticleByTitle(expectedFirstArticle.getTitle());

        Assertions.assertThat(actualArticle.get().getId()).isEqualTo(expectedFirstArticle.getId());
        Assertions.assertThat(actualArticle.get().getDateTimeOfCreation()).isEqualTo(expectedFirstArticle.getDateTimeOfCreation());
        Assertions.assertThat(actualArticle.get().getTitle()).isEqualTo(expectedFirstArticle.getTitle());
        Assertions.assertThat(actualArticle.get().getText()).isEqualTo(expectedFirstArticle.getText());
        Assertions.assertThat(actualArticle.get().getUsers().stream().findFirst().get().getUsername())
                .isEqualTo(expectedFirstArticle.getUsers().stream().findFirst().get().getUsername());
    }

    @Test
    void shouldFindOneArticleByKeywordInTitle() {
        Optional<List<Article>> actualArticle = articleRepository.findByTitleContains("First");

        Assertions.assertThat(actualArticle.get()).hasSize(1);

        Assertions.assertThat(actualArticle.get().get(0).getId()).isEqualTo(expectedFirstArticle.getId());
        Assertions.assertThat(actualArticle.get().get(0).getDateTimeOfCreation()).isEqualTo(expectedFirstArticle.getDateTimeOfCreation());
        Assertions.assertThat(actualArticle.get().get(0).getTitle()).isEqualTo(expectedFirstArticle.getTitle());
        Assertions.assertThat(actualArticle.get().get(0).getText()).isEqualTo(expectedFirstArticle.getText());
        Assertions.assertThat(actualArticle.get().get(0).getUsers().stream().findFirst().get().getUsername())
                .isEqualTo(expectedFirstArticle.getUsers().stream().findFirst().get().getUsername());
    }

    @Test
    void shouldFindTwoArticlesByKeywordInTitle() {
        Optional<List<Article>> actualArticle = articleRepository.findByTitleContains("test");

        Assertions.assertThat(actualArticle.get()).hasSize(2);

        Assertions.assertThat(actualArticle.get().get(0).getId()).isEqualTo(expectedFirstArticle.getId());
        Assertions.assertThat(actualArticle.get().get(0).getDateTimeOfCreation()).isEqualTo(expectedFirstArticle.getDateTimeOfCreation());
        Assertions.assertThat(actualArticle.get().get(0).getTitle()).isEqualTo(expectedFirstArticle.getTitle());
        Assertions.assertThat(actualArticle.get().get(0).getText()).isEqualTo(expectedFirstArticle.getText());
        Assertions.assertThat(actualArticle.get().get(0).getUsers().stream().findFirst().get().getUsername())
                .isEqualTo(expectedFirstArticle.getUsers().stream().findFirst().get().getUsername());

        Assertions.assertThat(actualArticle.get().get(1).getId()).isEqualTo(expectedSecondArticle.getId());
        Assertions.assertThat(actualArticle.get().get(1).getDateTimeOfCreation()).isEqualTo(expectedSecondArticle.getDateTimeOfCreation());
        Assertions.assertThat(actualArticle.get().get(1).getTitle()).isEqualTo(expectedSecondArticle.getTitle());
        Assertions.assertThat(actualArticle.get().get(1).getText()).isEqualTo(expectedSecondArticle.getText());
        Assertions.assertThat(actualArticle.get().get(1).getUsers().stream().findFirst().get().getUsername())
                .isEqualTo(expectedSecondArticle.getUsers().stream().findFirst().get().getUsername());
    }
}