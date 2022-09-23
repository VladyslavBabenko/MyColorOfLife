package com.github.vladyslavbabenko.mycoloroflife.entity;

import org.fest.assertions.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;

@DisplayName("Unit-level testing for Role")
class RoleTest {

    User testUser;

    Role testUserRole, testAdminRole, testAuthorRole;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1)
                .username("TestUser")
                .email("TestUser@mail.com")
                .password("123456")
                .build();

        testUserRole = Role.builder()
                .id(1)
                .roleName("ROLE_USER")
                .users(Collections.singleton(testUser))
                .description("Користувач")
                .build();

        testAdminRole = Role.builder()
                .id(2)
                .roleName("ROLE_ADMIN")
                .users(Collections.singleton(testUser))
                .description("Адміністратор")
                .build();

        testAuthorRole = Role.builder()
                .id(3)
                .roleName("ROLE_AUTHOR")
                .users(Collections.singleton(testUser))
                .description("Автор")
                .build();
    }

    @Test
    void testToStringUserRole() {
        Assertions.assertThat(testUserRole.toString()).isEqualTo("Користувач");
    }

    @Test
    void testToStringAdminRole() {
        Assertions.assertThat(testAdminRole.toString()).isEqualTo("Адміністратор");
    }

    @Test
    void testToStringAuthorRole() {
        Assertions.assertThat(testAuthorRole.toString()).isEqualTo("Автор");
    }

    @Test
    void testGetAuthorityUserRole() {
        Assertions.assertThat(testUserRole.getAuthority()).isEqualTo("ROLE_USER");
    }

    @Test
    void testGetAuthorityAdminRole() {
        Assertions.assertThat(testAdminRole.getAuthority()).isEqualTo("ROLE_ADMIN");
    }

    @Test
    void testGetAuthorityAuthorRole() {
        Assertions.assertThat(testAuthorRole.getAuthority()).isEqualTo("ROLE_AUTHOR");
    }
}