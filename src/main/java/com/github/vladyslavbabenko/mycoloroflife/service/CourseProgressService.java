package com.github.vladyslavbabenko.mycoloroflife.service;

import com.github.vladyslavbabenko.mycoloroflife.entity.Course;
import com.github.vladyslavbabenko.mycoloroflife.entity.CourseProgress;
import com.github.vladyslavbabenko.mycoloroflife.entity.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * {@link Service} for handling with {@link CourseProgress} entity.
 */

public interface CourseProgressService {
    /**
     * Get all {@link CourseProgress} entities.
     *
     * @return the collection of {@link CourseProgress} objects.
     */
    List<CourseProgress> getAllCourseProgress();

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

    /**
     * Save provided {@link CourseProgress} entity.
     *
     * @param courseProgressToSave provided CourseProgress to save.
     * @return true if operation was successful, otherwise false.
     */
    boolean save(CourseProgress courseProgressToSave);

    /**
     * Delete provided {@link CourseProgress} entity.
     *
     * @param courseProgressId id by which the CourseProgress will be deleted, if present
     * @return true if operation was successful, otherwise false.
     */
    boolean delete(Integer courseProgressId);

    /**
     * Update provided {@link CourseProgress} entity.
     *
     * @param updatedCourseProgress CourseProgress with updated fields that will replace the old ones.
     * @return true if operation was successful, otherwise false.
     */
    boolean update(CourseProgress updatedCourseProgress);
}
