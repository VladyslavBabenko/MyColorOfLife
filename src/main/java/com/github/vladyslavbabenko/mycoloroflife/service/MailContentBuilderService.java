package com.github.vladyslavbabenko.mycoloroflife.service;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * {@link Service} for building mail templates.
 */

public interface MailContentBuilderService {

    /**
     * Builds a Thymeleaf HTML template
     *
     * @param string string value to be added to the template
     * @param templatePath path to Thymeleaf HTML template
     */
    String build(String string, String templatePath);

    /**
     * Builds a Thymeleaf HTML template
     *
      @param strings A list of string values to be added to the template
     * @param templatePath path to Thymeleaf HTML template
     */
    String build(List<String> strings, String templatePath);
}