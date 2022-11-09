package com.github.vladyslavbabenko.mycoloroflife.service;

import com.github.vladyslavbabenko.mycoloroflife.entity.User;
import org.springframework.stereotype.Service;

/**
 * {@link Service} for handling with BruteForceProtection.
 */

public interface BruteForceProtectionService {
    /**
     * Method to register each failed login attempt for the given username.
     *
     * @param username {@link User}'s username
     */
    void registerLoginFailure(String username);

    /**
     * Method to reset the counter for successful login.
     *
     * @param username {@link User}'s username
     */
    void resetBruteForceCounter(String username);

    /**
     * Method to check if a user account is under brute force attack,
     * this will compare the number of failed attempts against a threshold.
     *
     * @param username {@link User}'s username
     * @return true if the number of failed attempts exceeds the threshold
     */
    boolean isBruteForceAttack(String username);
}