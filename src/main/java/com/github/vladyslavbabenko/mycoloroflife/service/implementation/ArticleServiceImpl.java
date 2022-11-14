package com.github.vladyslavbabenko.mycoloroflife.service.implementation;

import com.github.vladyslavbabenko.mycoloroflife.entity.Article;
import com.github.vladyslavbabenko.mycoloroflife.repository.ArticleRepository;
import com.github.vladyslavbabenko.mycoloroflife.service.ArticleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of {@link ArticleService}.
 */

@Service
public class ArticleServiceImpl implements ArticleService {

    private final ArticleRepository articleRepository;

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    public ArticleServiceImpl(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    @Override
    public List<Article> getAllArticles() {
        List<Article> articles = articleRepository.findAll();
        articles.sort(Comparator.comparingLong(Article::getId).reversed());
        return articles;
    }

    @Override
    public List<Article> findByKeyword(String keyword) {
        return articleRepository.findByTitleContains(keyword).orElse(Collections.emptyList());
    }

    @Override
    public Article findById(Integer articleId) {
        return articleRepository.findById(articleId).orElse(new Article());
    }

    @Override
    public boolean saveArticle(Article articleToSave) {
        if (articleRepository.existsByTitle(articleToSave.getTitle())) {
            return false;
        }

        articleRepository.save(articleToSave);

        log.info("Article with title {} saved successfully", articleToSave.getTitle());

        return true;

    }

    @Override
    public boolean deleteArticle(Integer articleId) {
        if (articleRepository.existsById(articleId)) {
            articleRepository.deleteById(articleId);

            log.info("Article with id {} deleted successfully", articleId);

            return true;
        }

        return false;
    }

    @Override
    public boolean updateArticle(Article updatedArticle) {
        Optional<Article> optionalArticle = articleRepository.findById(updatedArticle.getId());

        if (optionalArticle.isEmpty()) {
            return false;
        }

        Article articleToUpdate = optionalArticle.get();

        articleToUpdate.setTitle(updatedArticle.getTitle());
        articleToUpdate.setText(updatedArticle.getText());
        articleToUpdate.setDateTimeOfCreation(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm")));

        articleRepository.save(articleToUpdate);

        log.info("Article with title {} updated successfully", optionalArticle.get().getTitle());

        return true;
    }

    @Override
    public Optional<Article> optionalFindById(Integer articleId) {
        return articleRepository.findById(articleId);
    }
}