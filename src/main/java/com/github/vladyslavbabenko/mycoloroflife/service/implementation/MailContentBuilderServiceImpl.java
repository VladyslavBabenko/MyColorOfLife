package com.github.vladyslavbabenko.mycoloroflife.service.implementation;

import com.github.vladyslavbabenko.mycoloroflife.service.MailContentBuilderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.List;

/**
 * Implementation of {@link MailContentBuilderService}.
 */

@RequiredArgsConstructor
@Service
public class MailContentBuilderServiceImpl implements MailContentBuilderService {

    private final TemplateEngine templateEngine;

    @Override
    public String build(String string, String templatePath) {
        Context context = new Context();
        context.setVariable("string0", string);
        return templateEngine.process(templatePath, context);
    }

    @Override
    public String build(List<String> strings, String templatePath) {
        Context context = new Context();
        for (int i = 0; i < strings.size(); i++) {
            context.setVariable("string" + i, strings.get(i));
        }
        return templateEngine.process(templatePath, context);
    }
}