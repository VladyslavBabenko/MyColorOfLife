package com.github.vladyslavbabenko.mycoloroflife.repository;

import com.github.vladyslavbabenko.mycoloroflife.entity.ActivationCode;
import com.github.vladyslavbabenko.mycoloroflife.entity.CourseTitle;
import com.github.vladyslavbabenko.mycoloroflife.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * {@link Repository} for handling with {@link ActivationCode} entity.
 */

@Repository
public interface ActivationCodeRepository extends JpaRepository<ActivationCode, Integer> {
    /**
     * Finds all {@link ActivationCode} that belongs to {@link User}.
     *
     * @param user user to search
     * @return Optional List of ActivationCodes for provided user from database, otherwise empty Optional
     */
    Optional<List<ActivationCode>> findAllByUser(User user);

    /**
     * Finds {@link ActivationCode} by code
     *
     * @param code code to search
     * @return Optional ActivationCode from database, otherwise empty Optional
     */
    Optional<ActivationCode> findByCode(String code);

    /**
     * Finds all {@link ActivationCode} that activates {@link CourseTitle} and belongs to {@link User}.
     *
     * @param user        user to search
     * @param courseTitle courseTitle to search
     * @return Optional List of ActivationCodes for provided user and course from database, otherwise empty Optional
     */
    Optional<List<ActivationCode>> findAllByUserAndCourseTitle(User user, CourseTitle courseTitle);

    /**
     * Checks that {@link ActivationCode} exists by code.
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
     * Deletes all {@link ActivationCode} that belongs to {@link User}.
     *
     * @param user provided user to delete codes
     */
    void deleteAllByUser(User user);

    /**
     * Deletes all {@link ActivationCode} by code
     *
     * @param code code to delete
     */
    void deleteByCode(String code);
}