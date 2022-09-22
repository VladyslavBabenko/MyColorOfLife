package com.github.vladyslavbabenko.mycoloroflife.repository;

import com.github.vladyslavbabenko.mycoloroflife.entity.CourseTitle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * {@link Repository} for handling with {@link CourseTitle} entity.
 */

@Repository
public interface CourseTitleRepository extends JpaRepository<CourseTitle, Integer> {
    /**
     * Finds {@link CourseTitle} by title.
     *
     * @param title title to search
     * @return Optional CourseTitle from database, otherwise empty Optional
     */
    Optional<CourseTitle> findByTitle(String title);

    /**
     * Finds all {@link CourseTitle} by keyword.
     *
     * @param keyword keyword to search
     * @return Optional List of CourseTitles from database, otherwise empty Optional
     */
    Optional<List<CourseTitle>> findAllByTitleContains(String keyword);

    /**
     * Finds all {@link CourseTitle} by id.
     *
     * @param id id to search
     * @return Optional CourseTitle from database, otherwise empty Optional
     */
    Optional<CourseTitle> findById(int id);

    /**
     * Finds an {@link CourseTitle} by title.
     *
     * @param title title to search
     * @return true if exists, otherwise false
     */
    boolean existsByTitle(String title);
}