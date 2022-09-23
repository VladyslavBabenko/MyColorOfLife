package com.github.vladyslavbabenko.mycoloroflife.service;

import com.github.vladyslavbabenko.mycoloroflife.entity.ActivationCode;
import com.github.vladyslavbabenko.mycoloroflife.entity.Course;
import com.github.vladyslavbabenko.mycoloroflife.entity.CourseTitle;
import com.github.vladyslavbabenko.mycoloroflife.entity.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * {@link Service} for handling with {@link ActivationCode} entity.
 */

public interface ActivationCodeService {
    /**
     * Get all {@link ActivationCode} entities.
     *
     * @return the collection of {@link ActivationCode} objects.
     */
    List<ActivationCode> getAll();

    /**
     * Finds all {@link ActivationCode} that belongs to {@link User}.
     *
     * @param user user to search
     * @return List of existing {@link ActivationCode} from database
     */
    List<ActivationCode> findAllByUser(User user);

    /**
     * Find {@link ActivationCode} by ActivationCodeId.
     *
     * @param codeId provided ActivationCode id.
     * @return Optional ActivationCode for id from database if exists, otherwise empty Optional
     */
    Optional<ActivationCode> findById(Integer codeId);

    /**
     * Finds {@link ActivationCode} by code
     *
     * @param code code to search
     * @return ActivationCode from database, otherwise empty Optional
     */
    Optional<ActivationCode> findByCode(String code);

    /**
     * Finds {@link ActivationCode} that activates {@link CourseTitle} and belongs to {@link User}
     *
     * @param user        user to search
     * @param courseTitle course to search
     * @return Optional List of ActivationCodes for provided user and course from database, otherwise empty Optional
     */
    List<ActivationCode> findAllByUserAndCourseTitle(User user, CourseTitle courseTitle);

    /**
     * Finds an {@link ActivationCode} by code.
     *
     * @param code code to search
     * @return true if exists any activation code for provided string, otherwise false
     */
    boolean existsByCode(String code);

    /**
     * Checks that any {@link ActivationCode} exists by {@link User}.
     *
     * @param user user to search
     * @return true if exists any activation code for provided user, otherwise false
     */
    boolean existsByUser(User user);

    /**
     * Save provided {@link ActivationCode} entity.
     *
     * @param codeToSave provided ActivationCode to save.
     * @return true if operation was successful, otherwise false.
     */
    boolean save(ActivationCode codeToSave);

    /**
     * Delete provided {@link ActivationCode} entity.
     *
     * @param code {@link ActivationCode} code
     * @return true if operation was successful, otherwise false.
     */
    boolean deleteByCode(String code);

    /**
     * Deletes all {@link ActivationCode} that belongs to {@link User}.
     *
     * @param user provided user to delete codes
     * @return true if codes have been deleted, otherwise false
     */
    boolean deleteAllByUser(User user);

    /**
     * Update provided {@link ActivationCode} entity.
     *
     * @param updatedCode ActivationCode with updated fields that will replace the old ones.
     * @return true if operation was successful, otherwise false.
     */
    boolean update(ActivationCode updatedCode);

    /**
     * Creates and saves new {@link ActivationCode} entity for specified {@link Course} and {@link User}.
     *
     * @param user        the user for whom the code will be generated
     * @param courseTitle courseTitle to be assigned to the generated code
     * @return generated {@link ActivationCode}
     */
    ActivationCode createCode(CourseTitle courseTitle, User user);
}