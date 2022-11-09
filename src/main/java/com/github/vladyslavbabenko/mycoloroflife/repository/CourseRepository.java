package com.github.vladyslavbabenko.mycoloroflife.repository;

import com.github.vladyslavbabenko.mycoloroflife.entity.Course;
import com.github.vladyslavbabenko.mycoloroflife.entity.CourseTitle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * {@link Repository} for handling with {@link Course} entity.
 */

@Repository
public interface CourseRepository extends JpaRepository<Course, Integer> {
    /**
     * Finds all {@link Course} by {@link CourseTitle}.
     *
     * @param courseTitle courseTitle to search
     * @return Optional List of Courses from database, otherwise empty Optional
     */
    Optional<List<Course>> findAllByCourseTitle(CourseTitle courseTitle);

    /**
     * Finds an {@link Course} by title.
     *
     * @param title title to search
     * @return Optional Course from database, otherwise empty Optional
     */
    Optional<Course> findByVideoTitle(String title);

    /**
     * Finds all {@link Course} with keyword in title.
     *
     * @param keyword keyword to search
     * @return Optional List of Courses from database, otherwise empty Optional List
     */
    Optional<List<Course>> findAllByVideoTitleContains(String keyword);

    /**
     * Finds an {@link Course} by courseTitle and page.
     *
     * @param courseTitle courseTitle to search
     * @param page        page to search
     * @return Optional Course from database, otherwise empty Optional
     */
    Optional<Course> findByCourseTitleAndPage(CourseTitle courseTitle, Integer page);

    /**
     * Finds an {@link Course} by {@link CourseTitle}.
     *
     * @param courseTitle courseTitle to search
     * @return true if exists, otherwise false
     */
    boolean existsByCourseTitle(CourseTitle courseTitle);

    /**
     * Finds an {@link Course} by title.
     *
     * @param title title to search
     * @return true if exists, otherwise false
     */
    boolean existsByVideoTitle(String title);

    /**
     * Checks {@link Course} by courseTitle and page.
     *
     * @param courseTitle courseTitle to search
     * @param page        page to search
     * @return true if exists, otherwise false
     */
    boolean existsByCourseTitleAndPage(CourseTitle courseTitle, Integer page);
}