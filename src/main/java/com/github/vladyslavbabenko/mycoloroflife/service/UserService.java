package com.github.vladyslavbabenko.mycoloroflife.service;

import com.github.vladyslavbabenko.mycoloroflife.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * {@link Service} for handling with {@link User} entity.
 */

public interface UserService extends UserDetailsService {
    /**
     * Find {@link User} by userId.
     *
     * @param userId provided user id.
     * @return {@link User} with the provided id, or new {@link User} otherwise.
     */
    User findById(Integer userId);

    /**
     * Get all {@link User} entities.
     *
     * @return the collection of {@link User} objects.
     */
    List<User> getAllUsers();

    /**
     * Encodes input CharSequence.
     *
     * @param rawPassword raw user password.
     * @return encoded CharSequence converted to String.
     */
    String encodePassword(CharSequence rawPassword);

    /**
     * Save provided {@link User} entity.
     *
     * @param userToSave provided user to save.
     * @return true if operation was successful, otherwise false.
     */
    boolean saveUser(User userToSave);

    /**
     * Delete provided {@link User} entity.
     *
     * @param userId id by which the user will be deleted, if present
     * @return true if operation was successful, otherwise false.
     */
    boolean deleteUser(Integer userId);

    /**
     * Update provided {@link User} entity.
     *
     * @param updatedUser user with updated fields that will replace the old ones.
     * @return true if operation was successful, otherwise false.
     */
    boolean updateUser(User updatedUser);

    /**
     * Update provided {@link User} entity.
     *
     * @param updatedUser user with updated password that will replace the old one.
     * @return true if operation was successful, otherwise false.
     */
    boolean changePassword(User updatedUser);
}