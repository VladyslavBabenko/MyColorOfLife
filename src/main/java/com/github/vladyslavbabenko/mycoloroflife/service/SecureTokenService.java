package com.github.vladyslavbabenko.mycoloroflife.service;

import com.github.vladyslavbabenko.mycoloroflife.entity.SecureToken;
import com.github.vladyslavbabenko.mycoloroflife.entity.User;
import com.github.vladyslavbabenko.mycoloroflife.enumeration.Purpose;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * {@link Service} for handling with {@link SecureToken} entity.
 */

public interface SecureTokenService {

    /**
     * Get all {@link SecureToken} entities.
     *
     * @return the collection of {@link SecureToken} objects.
     */
    List<SecureToken> getAll();

    /**
     * Finds {@link SecureToken} by token.
     *
     * @param token token to search
     * @return Optional {@link SecureToken} if present, otherwise empty Optional
     */
    Optional<SecureToken> findByToken(String token);

    /**
     * Creates new {@link SecureToken} object
     *
     * @return new {@link SecureToken}
     */
    SecureToken createSecureToken();

    /**
     * Saves {@link SecureToken}
     *
     * @param token token to save
     * @return true if the operation was successful, otherwise false
     */
    boolean save(SecureToken token);

    /**
     * Deletes {@link SecureToken} by {@link SecureToken}
     *
     * @param token token to delete
     * @return true if the operation was successful, otherwise false
     */
    boolean delete(SecureToken token);

    /**
     * Deletes {@link SecureToken} by token
     *
     * @param token token to delete
     * @return true if the operation was successful, otherwise false
     */
    boolean delete(String token);

    /**
     * Deletes all expired  {@link SecureToken}s
     *
     * @return true if the operation was successful, otherwise false
     */
    boolean deleteAllExpired();

    /**
     * Updates {@link SecureToken}
     *
     * @param token token to update
     * @return true if the operation was successful, otherwise false
     */
    boolean update(SecureToken token);

    /**
     * Checks that exists a {@link SecureToken} for provided {@link User} and with {@link Purpose}
     *
     * @param user    user to search
     * @param purpose purpose to search
     * @return true if {@link SecureToken} exists by provided user and purpose, otherwise false
     */
    boolean existsByUserAndPurpose(User user, Purpose purpose);
}