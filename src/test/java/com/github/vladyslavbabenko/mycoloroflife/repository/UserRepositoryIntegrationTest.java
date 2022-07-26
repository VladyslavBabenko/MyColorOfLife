package com.github.vladyslavbabenko.mycoloroflife.repository;

import com.github.vladyslavbabenko.mycoloroflife.AbstractTest.AbstractRepositoryIntegrationTest;
import com.github.vladyslavbabenko.mycoloroflife.entity.User;
import org.fest.assertions.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;

@DisplayName("Integration-level testing for UserRepository")
@Sql(value = {"/create-test-values.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class UserRepositoryIntegrationTest extends AbstractRepositoryIntegrationTest {

    private User expectedUser;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        //given
        expectedUser = User.builder()
                .id(1)
                .name("TestUser")
                .email("testuser@mail.com")
                .build();
    }

    @Test
    void shouldProperlyFindByEmail() {
        //when
        Optional<User> userFromDB = userRepository.findByEmail(expectedUser.getEmail());

        //then
        Assertions.assertThat(userFromDB.get().getId()).isNotNull().isEqualTo(expectedUser.getId());
        Assertions.assertThat(userFromDB.get().getUsername()).isNotNull().isEqualTo(expectedUser.getUsername());
        Assertions.assertThat(userFromDB.get().getEmail()).isNotNull().isEqualTo(expectedUser.getEmail());
    }
}