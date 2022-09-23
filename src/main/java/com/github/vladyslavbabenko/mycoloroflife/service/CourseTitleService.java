package com.github.vladyslavbabenko.mycoloroflife.service;

import com.github.vladyslavbabenko.mycoloroflife.entity.CourseTitle;
import com.github.vladyslavbabenko.mycoloroflife.entity.Role;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * {@link Service} for handling with {@link CourseTitle} entity.
 */

public interface CourseTitleService {

    /**
     * Finds {@link CourseTitle} by title.
     *
     * @param title title to search
     * @return Optional {@link CourseTitle} from database
     */
    Optional<CourseTitle> findByTitle(String title);

    /**
     * Finds all {@link CourseTitle} with keyword in title.
     *
     * @param keyword keyword to search
     * @return List of existing {@link CourseTitle} from database
     */
    List<CourseTitle> findAllByCourseTitleContains(String keyword);

    /**
     * Get all {@link CourseTitle} entities.
     *
     * @return the collection of {@link CourseTitle} objects.
     */
    List<CourseTitle> getAllCourseTitles();

    /**
     * Find {@link CourseTitle} by CourseTitleId.
     *
     * @param courseTitleId provided CourseTitle id.
     * @return {@link CourseTitle} with the provided id, or new {@link CourseTitle} otherwise.
     */
    Optional<CourseTitle> findById(Integer courseTitleId);

    /**
     * Finds an {@link CourseTitle} by title
     *
     * @param title title to search
     * @return true if exists, otherwise false
     */
    boolean existsByTitle(String title);

    /**
     * Finds an {@link CourseTitle} by id
     *
     * @param id id to search
     * @return true if exists, otherwise false
     */
    boolean existsById(Integer id);

    /**
     * Save provided {@link CourseTitle} entity.
     *
     * @param courseTitleToSave provided CourseTitle to save.
     * @return true if operation was successful, otherwise false.
     */
    boolean save(CourseTitle courseTitleToSave);

    /**
     * Delete provided {@link CourseTitle} entity.
     *
     * @param courseTitleId id by which the CourseTitle will be deleted, if present
     * @return true if operation was successful, otherwise false.
     */
    boolean delete(Integer courseTitleId);

    /**
     * Update provided {@link CourseTitle} entity.
     *
     * @param updatedCourseTitle CourseTitle with updated fields that will replace the old ones.
     * @return true if operation was successful, otherwise false.
     */
    boolean update(CourseTitle updatedCourseTitle);

    /**
     * Converts provided courseTitle into a {@link Role}, and it can be saved into DB
     *
     * @param courseTitle courseTitle from which the {@link Role} will be created
     * @param saveToDB    flag to save the role in the database or not
     * @return new Role Object
     */
    Role createCourseOwnerRoleByTitle(CourseTitle courseTitle, boolean saveToDB);
}
