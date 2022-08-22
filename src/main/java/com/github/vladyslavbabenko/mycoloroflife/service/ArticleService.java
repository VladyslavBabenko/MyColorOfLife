package com.github.vladyslavbabenko.mycoloroflife.service;

import com.github.vladyslavbabenko.mycoloroflife.entity.Article;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * {@link Service} for handling with {@link Article} entity.
 */

public interface ArticleService {
    /**
     * Get all {@link Article} entities.
     *
     * @return the collection of {@link Article} objects.
     */
    List<Article> getAllArticles();

    /**
     * Finds all {@link Article} with keyword in title or text.
     *
     * @param keyword keyword to search
     * @return List of existing articles from database
     */
    List<Article> findByKeyword(String keyword);

    /**
     * Find {@link Article} by articleId.
     *
     * @param articleId provided article id.
     * @return {@link Article} with the provided id, or new {@link Article} otherwise.
     */
    Article findById(Integer articleId);

    /**
     * Save provided {@link Article} entity.
     *
     * @param articleToSave provided article to save.
     * @return true if operation was successful, otherwise false.
     */
    boolean saveArticle(Article articleToSave);

    /**
     * Delete provided {@link Article} entity.
     *
     * @param articleId id by which the article will be deleted, if present
     * @return true if operation was successful, otherwise false.
     */
    boolean deleteArticle(Integer articleId);

    /**
     * Update provided {@link Article} entity.
     *
     * @param updatedArticle article with updated fields that will replace the old ones.
     * @return true if operation was successful, otherwise false.
     */
    boolean updateArticle(Article updatedArticle);
}
