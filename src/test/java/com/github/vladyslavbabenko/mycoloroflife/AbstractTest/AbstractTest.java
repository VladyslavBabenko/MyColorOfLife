package com.github.vladyslavbabenko.mycoloroflife.AbstractTest;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(value = {"classpath:application-test.properties", "classpath:messages.properties", "classpath:templates.properties"})
public abstract class AbstractTest {
}