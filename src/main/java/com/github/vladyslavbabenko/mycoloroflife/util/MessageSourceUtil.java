package com.github.vladyslavbabenko.mycoloroflife.util;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * Util class to autowire messages from .properties files
 */

@Component
@RequiredArgsConstructor
public class MessageSourceUtil {

    private final MessageSource messageSource;

    public String getMessage(String source) {
        return messageSource.getMessage(source, null, LocaleContextHolder.getLocale());
    }

    public String getMessage(String source, Object[] args) {
        return messageSource.getMessage(source, args, LocaleContextHolder.getLocale());
    }

    public String getMessage(String source, Object[] args, Locale locale) {
        return messageSource.getMessage(source, args, locale);
    }
}