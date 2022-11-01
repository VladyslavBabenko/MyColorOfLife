package com.github.vladyslavbabenko.mycoloroflife.entity;

import org.fest.assertions.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

class UserTest {

    Role testUserRole;
    User testUser;

    @BeforeEach
    void setUp() {
        testUserRole = Role.builder()
                .id(1)
                .roleName("ROLE_USER")
                .build();

        testUser = User.builder()
                .id(1)
                .name("TestUser")
                .email("TestUser@mail.com")
                .password("123456")
                .roles(Collections.singleton(testUserRole))
                .build();

        testUserRole.setUsers(Collections.singleton(testUser));
    }

    @Test
    void testGetAuthorities() {
        Assertions.assertThat(testUser.getAuthorities().contains(testUserRole)).isTrue();
    }
}