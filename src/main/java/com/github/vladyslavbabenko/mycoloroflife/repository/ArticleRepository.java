package com.github.vladyslavbabenko.mycoloroflife.repository;

import com.github.vladyslavbabenko.mycoloroflife.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * {@link Repository} for handling with {@link Article} entity.
 */

@Repository
public interface ArticleRepository extends JpaRepository<Article, Integer> {
    /**
     * Finds an {@link Article} by title.
     *
     * @param title title to search
     * @return Optional article from database, otherwise empty Optional
     */
    Optional<Article> findArticleByTitle(String title);

    /**
     * Finds all {@link Article} with keyword in title.
     *
     * @param keyword keyword to search
     * @return List of articles from database, otherwise empty List
     */
    Optional<List<Article>> findByTitleContains(String keyword);

    /**
     * Finds an {@link Article} by title.
     *
     * @param title title to search
     * @return true if exists, otherwise false
     */
    boolean existsByTitle(String title);
}