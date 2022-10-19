package com.github.vladyslavbabenko.mycoloroflife.service;

import com.github.vladyslavbabenko.mycoloroflife.entity.SecureToken;
import com.github.vladyslavbabenko.mycoloroflife.entity.User;
import org.springframework.stereotype.Service;

/**
 * {@link Service} for handling with {@link User} password recovery.
 */

public interface PasswordRecoveryService {
    /**
     * Sends an email to the {@link User}'s email address with a password reset link
     *
     * @param username {@link User}'s username
     * @return true if mail was sent, false otherwise.
     */
    boolean forgottenPassword(String username);

    /**
     * If {@link SecureToken} is correct and has not expired yet, updates {@link User}'s password
     *
     * @param password new {@link User}'s password
     * @param token    {@link SecureToken} as a string to confirm that only the owner can change the password
     * @return true if operation was successful, otherwise false.
     */
    boolean updatePassword(String password, String token);

    /**
     * Checks if the login is disabled or not.
     * If disabled, an email is sent with a password reset link
     *
     * @param username {@link User}'s username
     * @return true if login is disabled, otherwise false.
     */
    boolean loginDisabled(String username);
}
