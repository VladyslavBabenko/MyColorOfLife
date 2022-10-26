package com.github.vladyslavbabenko.mycoloroflife.enumeration;

import com.github.vladyslavbabenko.mycoloroflife.entity.SecureToken;
import com.github.vladyslavbabenko.mycoloroflife.entity.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Enumeration for handling {@link SecureToken} purpose
 */

@Getter
@ToString
@RequiredArgsConstructor
public enum Purpose {
    /**
     * NONE - When a {@link SecureToken} has no purpose
     */
    NONE("None"),

    /**
     * PASSWORD_RECOVERY - When a {@link User} needs to recover their password
     */
    PASSWORD_RECOVERY("Password Recovery"),

    /**
     * EMAIL_CONFIRM - When a {@link User} needs to verify an email address
     */
    EMAIL_CONFIRM("Email Confirm");

    private final String purpose;
}