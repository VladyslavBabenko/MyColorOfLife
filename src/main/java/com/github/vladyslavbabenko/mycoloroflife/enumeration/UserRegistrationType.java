package com.github.vladyslavbabenko.mycoloroflife.enumeration;

import com.github.vladyslavbabenko.mycoloroflife.entity.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Enumeration for handling {@link User} Registration Type
 */

@Getter
@ToString
@RequiredArgsConstructor
public enum UserRegistrationType {
    /**
     * REGISTRATION_FORM - When a {@link User} registers via the standard registration form
     */
    REGISTRATION_FORM("Registration Form"),
    /**
     * GMAIL_AUTHENTICATION - When a {@link User} registers via gmail
     */
    GMAIL_AUTHENTICATION("Gmail Authentication");

    private final String registrationType;
}