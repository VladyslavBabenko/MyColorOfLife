package com.github.vladyslavbabenko.mycoloroflife.service;

import com.github.vladyslavbabenko.mycoloroflife.entity.ActivationCode;
import com.github.vladyslavbabenko.mycoloroflife.entity.Role;
import com.github.vladyslavbabenko.mycoloroflife.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
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
     * Find {@link User} by userId.
     *
     * @param userId provided user id.
     * @return true if {@link User} exists, false otherwise.
     */
    boolean existsById(Integer userId);

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

    /**
     * @return {@link User} entity from the current session.
     */
    User getCurrentUser();

    /**
     * Update provided {@link User} entity.
     *
     * @param user        the {@link User} whose password will be checked.
     * @param rawPassword raw password that will be checked for matching {@link User} password;
     * @return true if rawPassword matches {@link User} password, otherwise false.
     */
    boolean matchesPassword(User user, CharSequence rawPassword);

    /**
     * Save provided {@link OAuth2UserAuthority} as {@link User} entity.
     *
     * @param oAuth2UserAuthority provided {@link OAuth2UserAuthority} to save as {@link User} entity.
     * @return true if operation was successful, otherwise false.
     */
    boolean saveOAuth2User(OAuth2UserAuthority oAuth2UserAuthority);

    /**
     * Activates provided {@link ActivationCode}
     *
     * @param activationCode activationCode to activate
     * @return true if operation was successful, otherwise false.
     */
    boolean activateCode(ActivationCode activationCode);

    /**
     * Deletes {@link Role} from provided {@link User} List
     *
     * @param users users whose role will be removed
     * @param roleToDelete role to remove from users
     * @return true if operation was successful, otherwise false.
     */
    boolean deleteRoleFromUser(List<User> users, Role roleToDelete);
}