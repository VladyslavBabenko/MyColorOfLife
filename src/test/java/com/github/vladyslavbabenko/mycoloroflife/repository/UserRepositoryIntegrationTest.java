package com.github.vladyslavbabenko.mycoloroflife.repository;

import com.github.vladyslavbabenko.mycoloroflife.entity.User;
import org.fest.assertions.api.Assertions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@ActiveProfiles("test")
@DataJpaTest
@DisplayName("Integration-level testing for UserService.")
@AutoConfigureTestDatabase(replace = NONE)
class UserRepositoryIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Sql(scripts = {"/sql/clearDB.sql","/sql/addUsers.sql", "/sql/addRoles.sql"})
    @Test
    void shouldProperlyfindByUsername() {
        //when
        User userFromDB = userRepository.findByUsername("TestUser1");

        //given
        User expectedUser = User.builder()
                .id(userFromDB.getId())
                .username("TestUser1")
                .email("TestUser1Mail@mail.com")
                .password(String.valueOf(123456789))
                .build();
        //then
        Assertions.assertThat(userFromDB).isNotNull().isEqualTo(expectedUser);
    }

    @Sql(scripts = {"/sql/clearDB.sql","/sql/addUsers.sql", "/sql/addRoles.sql"})
    @Test
    void shouldProperlyfindByEmail() {
        //when
        User userFromDB = userRepository.findByEmail("TestUser1Mail@mail.com");

        //given
        User expectedUser = User.builder()
                .id(userFromDB.getId())
                .username("TestUser1")
                .email("TestUser1Mail@mail.com")
                .password(String.valueOf(123456789))
                .build();

        //then
        Assertions.assertThat(userFromDB).isNotNull().isEqualTo(expectedUser);
    }

    @Sql(scripts = {"/sql/clearDB.sql","/sql/addUsers.sql", "/sql/addRoles.sql"})
    @Test
    void shouldProperlyfindAllUsers() {
        //when
        List<User> userList = userRepository.findAll();

        //then
        Assertions.assertThat(userList).isNotNull().hasSize(4);
    }
}