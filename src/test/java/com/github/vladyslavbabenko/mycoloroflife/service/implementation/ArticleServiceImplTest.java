package com.github.vladyslavbabenko.mycoloroflife.service.implementation;

import com.github.vladyslavbabenko.mycoloroflife.entity.Article;
import com.github.vladyslavbabenko.mycoloroflife.entity.User;
import com.github.vladyslavbabenko.mycoloroflife.repository.ArticleRepository;
import com.github.vladyslavbabenko.mycoloroflife.service.ArticleService;
import org.fest.assertions.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.Optional;

@DisplayName("Unit-level testing for ArticleService")
class ArticleServiceImplTest {

    private User testAuthor;
    private ArticleService articleService;
    private ArticleRepository articleRepository;
    private Article firstTestArticle;

    @BeforeEach
    void setUp() {
        //given
        articleRepository = Mockito.mock(ArticleRepository.class);
        articleService = new ArticleServiceImpl(articleRepository);
        testAuthor = User.builder()
                .id(3)
                .username("TestAuthor")
                .email("TestAuthor@mail.com")
                .build();

        firstTestArticle = Article.builder()
                .id(1)
                .dateTimeOfCreation("2022.08.05 22:00")
                .title("First test title")
                .text("First test text. First test text. First test text.")
                .users(Collections.singleton(testAuthor))
                .build();
    }

    @Test
    void isArticleServiceImplTestReady() {
        Assertions.assertThat(articleRepository).isNotNull().isInstanceOf(ArticleRepository.class);
        Assertions.assertThat(articleService).isNotNull().isInstanceOf(ArticleService.class);
        Assertions.assertThat(testAuthor).isNotNull().isInstanceOf(User.class);
    }

    @Test
    void shouldGetAllArticles() {
        //when
        articleService.getAllArticles();

        Mockito.verify(articleRepository, Mockito.times(1)).findAll();
    }

    @Test
    void shouldFindByKeyword() {
        String keyword = "First";

        //when
        articleService.findByKeyword(keyword);

        Mockito.doReturn(Optional.of(firstTestArticle))
                .when(articleRepository)
                .findByTitleContains(keyword);

        Mockito.verify(articleRepository, Mockito.times(1)).findByTitleContains(keyword);
    }

    @Test
    void shouldFIndArticleById() {
        //when
        Mockito.doReturn(Optional.of(firstTestArticle))
                .when(articleRepository)
                .findById(firstTestArticle.getId());

        articleService.findById(firstTestArticle.getId());

        //then
        Mockito.verify(articleRepository, Mockito.times(1)).findById(firstTestArticle.getId());
    }

    @Test
    void shouldSaveArticle() {
        //when
        Mockito.doReturn(false)
                .when(articleRepository)
                .existsByTitle(firstTestArticle.getTitle());

        boolean isSaved = articleService.saveArticle(firstTestArticle);

        //then
        Mockito.verify(articleRepository, Mockito.times(1)).save(firstTestArticle);
        Assertions.assertThat(isSaved).isTrue();
    }

    @Test
    void shouldNotSaveArticle() {
        //when
        Mockito.doReturn(true)
                .when(articleRepository)
                .existsByTitle(firstTestArticle.getTitle());

        boolean isSaved = articleService.saveArticle(firstTestArticle);

        //then
        Mockito.verify(articleRepository, Mockito.times(0)).save(firstTestArticle);
        Assertions.assertThat(isSaved).isFalse();
    }

    @Test
    void shouldNotDeleteArticle() {
        //when
        boolean isArticleDeleted = articleService.deleteArticle(firstTestArticle.getId());

        //then
        Mockito.verify(articleRepository, Mockito.times(1)).existsById(firstTestArticle.getId());
        Mockito.verify(articleRepository, Mockito.times(0)).deleteById(firstTestArticle.getId());
        Assertions.assertThat(isArticleDeleted).isFalse();
    }

    @Test
    void shouldDeleteArticle() {
        //when
        Mockito.doReturn(true).when(articleRepository).existsById(firstTestArticle.getId());
        boolean isArticleDeleted = articleService.deleteArticle(firstTestArticle.getId());

        //then
        Mockito.verify(articleRepository, Mockito.times(1)).existsById(firstTestArticle.getId());
        Mockito.verify(articleRepository, Mockito.times(1)).deleteById(firstTestArticle.getId());
        Assertions.assertThat(isArticleDeleted).isTrue();
    }

    @Test
    void shouldUpdateArticle() {
        //when
        Mockito.doReturn(Optional.of(firstTestArticle)).when(articleRepository).findById(firstTestArticle.getId());
        boolean isArticleUpdated = articleService.updateArticle(firstTestArticle);

        //then
        Mockito.verify(articleRepository, Mockito.times(1)).save(firstTestArticle);
        Assertions.assertThat(isArticleUpdated).isTrue();
    }

    @Test
    void shouldNotUpdateArticle() {
        //when
        Mockito.doReturn(Optional.empty()).when(articleRepository).findById(firstTestArticle.getId());
        boolean isArticleUpdated = articleService.updateArticle(firstTestArticle);

        //then
        Mockito.verify(articleRepository, Mockito.times(0)).save(firstTestArticle);
        Assertions.assertThat(isArticleUpdated).isFalse();
    }
}