package com.github.vladyslavbabenko.mycoloroflife.util;

import com.github.vladyslavbabenko.mycoloroflife.AbstractTest.AbstractTest;
import org.fest.assertions.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

@DisplayName("Unit-level testing for MessageSourceUtil")
class MessageSourceUtilTest extends AbstractTest {

    private MessageSource messageSource;
    private MessageSourceUtil messageSourceUtil;

    @Value("${lang.uk}")
    private String source;

    private Object[] args;
    private Locale locale;

    @BeforeEach
    void setUp() {
        //given
        messageSource = Mockito.mock(MessageSource.class);

        messageSourceUtil = new MessageSourceUtil(messageSource);

        args = new Object[]{new Object()};
        locale = Locale.ENGLISH;
    }

    @Test
    void isMessageSourceUtilTestReady() {
        Assertions.assertThat(messageSource).isNotNull().isInstanceOf(MessageSource.class);
        Assertions.assertThat(messageSourceUtil).isNotNull().isInstanceOf(MessageSourceUtil.class);
    }

    @Test
    void getMessage_With_1_Arg() {
        //when
        messageSourceUtil.getMessage(source);

        //then
        Mockito.verify(messageSource, Mockito.times(1)).getMessage(source, null, LocaleContextHolder.getLocale());
    }

    @Test
    void getMessage_With_2_Args() {
        //when
        messageSourceUtil.getMessage(source, args);

        //then
        Mockito.verify(messageSource, Mockito.times(1)).getMessage(source, args, LocaleContextHolder.getLocale());
    }

    @Test
    void getMessage_With_3_Args() {
        //when
        messageSourceUtil.getMessage(source, args, locale);

        //then
        Mockito.verify(messageSource, Mockito.times(1)).getMessage(source, args, locale);
    }
}