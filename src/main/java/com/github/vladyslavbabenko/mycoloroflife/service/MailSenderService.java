package com.github.vladyslavbabenko.mycoloroflife.service;

import org.springframework.stereotype.Service;

/**
 * {@link Service} for sending mail.
 */

public interface MailSenderService {

    /**
     * Sends an email
     *
     * @param to      where email will be sent
     * @param subject subject of an email
     * @param text    email text
     */
    boolean sendEmail(String to, String subject, String text);

    /**
     * Sends an email
     *
     * @param to      where email will be sent
     * @param from    from what email will be sent
     * @param subject subject of an email
     * @param text    email text
     */
    boolean sendEmail(String to, String from, String subject, String text);
}
