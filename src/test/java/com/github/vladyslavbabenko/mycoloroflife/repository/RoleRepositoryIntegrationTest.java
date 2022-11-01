package com.github.vladyslavbabenko.mycoloroflife.repository;

import com.github.vladyslavbabenko.mycoloroflife.entity.Role;
import org.fest.assertions.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;

@DisplayName("Integration-level testing for RoleRepository")
@Sql(value = {"/create-test-values.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class RoleRepositoryIntegrationTest extends AbstractRepositoryIntegrationTest {

    private Role expectedRole;

    @Autowired
    private RoleRepository roleRepository;

    @BeforeEach
    void setUp() {
        //given
        expectedRole = Role.builder().id(4).roleName("ROLE_COURSE_OWNER_TEST").build();
    }

    @Test
    void findByRoleName() {
        //when
        Optional<Role> actualRole = roleRepository.findByRoleName(expectedRole.getRoleName());

        //then
        Assertions.assertThat(actualRole.get().getRoleName()).isEqualTo(expectedRole.getRoleName());
    }

    @Test
    void existsByRoleName() {
        //when
        boolean exists = roleRepository.existsByRoleName(expectedRole.getRoleName());

        //then
        Assertions.assertThat(exists).isTrue();
    }
}