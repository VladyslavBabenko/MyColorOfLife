package com.github.vladyslavbabenko.mycoloroflife.repository;

import com.github.vladyslavbabenko.mycoloroflife.AbstractTest.AbstractRepositoryIntegrationTest;
import com.github.vladyslavbabenko.mycoloroflife.entity.SecureToken;
import com.github.vladyslavbabenko.mycoloroflife.entity.User;
import org.fest.assertions.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;

@DisplayName("Integration-level testing for SecureTokenRepository")
@Sql(value = {"/create-test-values.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class SecureTokenRepositoryIntegrationTest extends AbstractRepositoryIntegrationTest {

    private SecureToken expectedSecureToken;
    private User expectedUser;

    @Autowired
    private SecureTokenRepository secureTokenRepository;

    @BeforeEach
    void setUp() {
        //given
        expectedUser = User.builder()
                .id(1)
                .name("TestUser")
                .email("TestUser@mail.com")
                .build();
        expectedSecureToken = SecureToken.builder().id(1).token("wMQzFUNrjsXyyht0lF-B").user(expectedUser).build();
    }


    @Test
    void findByToken() {
        //when
        Optional<SecureToken> actualToken = secureTokenRepository.findByToken(expectedSecureToken.getToken());

        //then
        Assertions.assertThat(actualToken.get()).isEqualTo(expectedSecureToken);
    }

    @Test
    void deleteByToken() {
        //when
        secureTokenRepository.deleteByToken(expectedSecureToken.getToken());

        Optional<SecureToken> actualToken = secureTokenRepository.findByToken(expectedSecureToken.getToken());

        //then
        Assertions.assertThat(actualToken.isEmpty()).isTrue();
    }
}