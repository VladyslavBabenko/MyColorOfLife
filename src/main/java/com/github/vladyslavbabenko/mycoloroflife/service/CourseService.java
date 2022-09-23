package com.github.vladyslavbabenko.mycoloroflife.service;

import com.github.vladyslavbabenko.mycoloroflife.entity.Course;
import com.github.vladyslavbabenko.mycoloroflife.entity.CourseTitle;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * {@link Service} for handling with {@link Course} entity.
 */

public interface CourseService {
    /**
     * Get all {@link Course} entities.
     *
     * @return the collection of {@link Course} objects.
     */
    List<Course> getAllCourses();

    /**
     * Finds all {@link Course} with keyword in course title.
     *
     * @param title title to search
     * @return List of existing {@link Course} from database
     */
    List<Course> findAllByCourseTitleContains(String title);

    /**
     * Finds all {@link Course} with courseTitle as course title.
     *
     * @param courseTitle courseTitle to search
     * @return List of existing {@link Course} from database
     */
    List<Course> findAllByCourseTitle(CourseTitle courseTitle);

    /**
     * Find {@link Course} by CourseId.
     *
     * @param courseId provided Course id.
     * @return {@link Course} with the provided id, or new {@link Course} otherwise.
     */
    Course findById(Integer courseId);

    /**
     * Save provided {@link Course} entity.
     *
     * @param courseToSave provided Course to save.
     * @return true if operation was successful, otherwise false.
     */
    boolean save(Course courseToSave);

    /**
     * Delete provided {@link Course} entity.
     *
     * @param courseId id by which the Course will be deleted, if present
     * @return true if operation was successful, otherwise false.
     */
    boolean delete(Integer courseId);

    /**
     * Update provided {@link Course} entity.
     *
     * @param updatedCourse Course with updated fields that will replace the old ones.
     * @return true if operation was successful, otherwise false.
     */
    boolean update(Course updatedCourse);

    /**
     * Finds all {@link Course} with keyword in title.
     *
     * @param keyword keyword to search
     * @return List of Courses from database, otherwise empty List
     */
    Optional<List<Course>> findAllByVideoTitleContains(String keyword);

    /**
     * Finds an {@link Course} by courseTitle and page.
     *
     * @param title title to search
     * @param page  page to search
     * @return Optional Course from database, otherwise empty Optional
     */
    Optional<Course> findByCourseTitleAndPage(String title, Integer page);

    /**
     * Finds an {@link Course} by {@link CourseTitle}.
     *
     * @param courseTitle courseTitle to search
     * @return true if exists, otherwise false
     */
    boolean existsByCourseTitle(CourseTitle courseTitle);
}