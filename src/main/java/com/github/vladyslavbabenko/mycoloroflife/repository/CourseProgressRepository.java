package com.github.vladyslavbabenko.mycoloroflife.repository;

import com.github.vladyslavbabenko.mycoloroflife.entity.Course;
import com.github.vladyslavbabenko.mycoloroflife.entity.CourseProgress;
import com.github.vladyslavbabenko.mycoloroflife.entity.CourseTitle;
import com.github.vladyslavbabenko.mycoloroflife.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * {@link Repository} for handling with {@link CourseProgress} entity.
 */
@Repository
public interface CourseProgressRepository extends JpaRepository<CourseProgress, Integer> {
    /**
     * Finds all {@link CourseProgress} by {@link User}.
     *
     * @param user user to search
     * @return Optional List of CourseProgress from database, otherwise empty Optional
     */
    Optional<List<CourseProgress>> findAllByUser(User user);

    /**
     * Finds an {@link CourseProgress} by {@link User} and {@link Course}.
     *
     * @param user   user to search
     * @param course course to search
     * @return Optional CourseProgress from database, otherwise empty Optional
     */
    Optional<CourseProgress> findByUserAndCourse(User user, Course course);

    /**
     * Finds an {@link CourseProgress} by {@link User} and {@link Course}.
     *
     * @param user   user to search
     * @param course course to search
     * @return true if exists, otherwise false
     */
    boolean existsByUserAndCourse(User user, Course course);

    /**
     * Finds an {@link CourseProgress} by {@link User}
     *
     * @param user user to search
     * @return true if exists, otherwise false
     */
    boolean existsByUser(User user);
}