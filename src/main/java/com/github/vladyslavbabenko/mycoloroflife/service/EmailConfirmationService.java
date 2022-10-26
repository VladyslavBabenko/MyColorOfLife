package com.github.vladyslavbabenko.mycoloroflife.service;

import com.github.vladyslavbabenko.mycoloroflife.entity.User;

public interface EmailConfirmationService {

    /**
     * If the token is valid, confirms the {@link User}'s email address
     *
     * @param token token to check
     * @return true if the email was confirmed, false otherwise
     */
    boolean confirmEmail(String token);

    /**
     * Sends an email with a confirmation link
     *
     * @param user To whom the letter will be sent
     */
    void sendConfirmationEmail(User user);
}