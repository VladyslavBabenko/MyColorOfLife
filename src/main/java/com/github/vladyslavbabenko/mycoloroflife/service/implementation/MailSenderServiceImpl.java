package com.github.vladyslavbabenko.mycoloroflife.service.implementation;

import com.github.vladyslavbabenko.mycoloroflife.service.MailSenderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * Implementation of {@link MailSenderService}.
 */

@RequiredArgsConstructor
@Service
public class MailSenderServiceImpl implements MailSenderService {
    private final JavaMailSender mailSender;

    @Value("${EMAIL_USERNAME}")
    private String fromEmail;

    @Override
    public boolean sendEmail(String to, String subject, String text) {
        return sendMimeMessage(to, fromEmail, subject, text);
    }

    @Override
    public boolean sendEmail(String to, String from, String subject, String text) {
        return sendMimeMessage(to, from, subject, text);
    }

    private boolean sendMimeMessage(String to, String from, String subject, String text) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, "UTF-8");
        try {
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setFrom(from);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(text, true);

            mailSender.send(mimeMessage);
            return true;
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
    }
}
