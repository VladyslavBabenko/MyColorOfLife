package com.github.vladyslavbabenko.mycoloroflife.service;

import com.github.vladyslavbabenko.mycoloroflife.entity.Role;
import com.github.vladyslavbabenko.mycoloroflife.repository.RoleRepository;
import org.fest.assertions.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

@DisplayName("Unit-level testing for RoleService")
class RoleServiceImplTest {
    private RoleRepository roleRepository;
    private RoleService roleService;

    private Role testRole, testRole_2;

    @BeforeEach
    void setUp() {
        //given
        roleRepository = Mockito.mock(RoleRepository.class);
        roleService = new RoleServiceImpl(roleRepository);

        testRole = Role.builder().id(1).roleName("ROLE_USER").build();
        testRole_2 = Role.builder().id(2).roleName("ROLE_AUTHOR").build();
    }

    @Test
    void isRoleServiceImplTestReady() {
        Assertions.assertThat(roleRepository).isNotNull().isInstanceOf(RoleRepository.class);
        Assertions.assertThat(roleService).isNotNull().isInstanceOf(RoleService.class);
        Assertions.assertThat(testRole).isNotNull().isInstanceOf(Role.class);
        Assertions.assertThat(testRole_2).isNotNull().isInstanceOf(Role.class);
    }

    @Test
    void findById() {
        //when
        roleService.findById(testRole.getId());

        //then
        Mockito.verify(roleRepository, Mockito.times(1)).findById(testRole.getId());
    }

    @Test
    void getAllRoles() {
        //when
        roleService.getAllRoles();

        //then
        Mockito.verify(roleRepository, Mockito.times(1)).findAll();
        Assertions.assertThat(roleService.getAllRoles())
                .isNotNull()
                .isInstanceOf(List.class)
                .isSorted();
    }

    @Test
    void findByRoleName() {
        //when
        roleService.findByRoleName(testRole.getRoleName());

        //then
        Mockito.verify(roleRepository, Mockito.times(1)).findByRoleName(testRole.getRoleName());
    }

    @Test
    void existsByRoleName() {
        //when
        roleService.existsByRoleName(testRole.getRoleName());

        //then
        Mockito.verify(roleRepository, Mockito.times(1)).existsByRoleName(testRole.getRoleName());
    }

    @Test
    void save_Success() {
        //when
        boolean saved = roleService.save(testRole);

        //then
        Mockito.verify(roleRepository, Mockito.times(1)).findByRoleName(testRole.getRoleName());
        Mockito.verify(roleRepository, Mockito.times(1)).save(testRole);
        Assertions.assertThat(saved).isTrue();
    }

    @Test
    void save_Failure_FindById_isPresent() {
        //given
        testRole.setRoleName(null);
        Mockito.doReturn(Optional.of(testRole)).when(roleRepository).findById(testRole.getId());

        //when
        boolean saved = roleService.save(testRole);

        //then
        Mockito.verify(roleRepository, Mockito.times(1)).findById(testRole.getId());
        Mockito.verify(roleRepository, Mockito.times(0)).save(testRole);
        Assertions.assertThat(saved).isFalse();
    }

    @Test
    void save_Failure_FindByRoleName_isPresent() {
        //given
        Mockito.doReturn(Optional.of(testRole)).when(roleRepository).findByRoleName(testRole.getRoleName());

        //when
        boolean saved = roleService.save(testRole);

        //then
        Mockito.verify(roleRepository, Mockito.times(1)).findByRoleName(testRole.getRoleName());
        Mockito.verify(roleRepository, Mockito.times(0)).save(testRole);
        Assertions.assertThat(saved).isFalse();
    }

    @Test
    void update_With_1_Parameter_Success() {
        //given
        Mockito.doReturn(Optional.of(testRole_2)).when(roleRepository).findById(testRole_2.getId());

        //when
        boolean updated = roleService.update(testRole_2);

        //then
        Mockito.verify(roleRepository, Mockito.times(1)).findById(testRole_2.getId());
        Mockito.verify(roleRepository, Mockito.times(1)).save(testRole_2);
        Assertions.assertThat(updated).isTrue();
    }

    @Test
    void update_With_1_Parameter_NoRoleInDB() {
        //when
        boolean updated = roleService.update(testRole_2);

        //then
        Mockito.verify(roleRepository, Mockito.times(1)).findById(testRole_2.getId());
        Mockito.verify(roleRepository, Mockito.times(0)).save(testRole_2);
        Assertions.assertThat(updated).isFalse();
    }

    @Test
    void update_With_2_Parameters_Success() {
        //given
        String testRoleName = testRole.getRoleName();
        Mockito.doReturn(Optional.of(testRole)).when(roleRepository).findByRoleName(testRoleName);

        //when
        boolean updated = roleService.update(testRole, testRole_2);

        //then
        Mockito.verify(roleRepository, Mockito.times(1)).findByRoleName(testRoleName);
        Mockito.verify(roleRepository, Mockito.times(1)).save(testRole);
        Assertions.assertThat(updated).isTrue();
    }

    @Test
    void update_With_2_Parameters_NoRoleInDB() {
        //when
        boolean updated = roleService.update(testRole, testRole_2);

        //then
        Mockito.verify(roleRepository, Mockito.times(1)).findByRoleName(testRole.getRoleName());
        Mockito.verify(roleRepository, Mockito.times(0)).save(testRole);
        Assertions.assertThat(updated).isFalse();
    }

    @Test
    void deleteSuccess() {
        //given
        Mockito.doReturn(true).when(roleRepository).existsByRoleName(testRole.getRoleName());
        Mockito.doReturn(Optional.of(testRole)).when(roleRepository).findByRoleName(testRole.getRoleName());

        //when
        boolean deleted = roleService.delete(testRole);

        //then
        Mockito.verify(roleRepository, Mockito.times(1)).existsByRoleName(testRole.getRoleName());
        Mockito.verify(roleRepository, Mockito.times(1)).findByRoleName(testRole.getRoleName());
        Mockito.verify(roleRepository, Mockito.times(1)).delete(testRole);
        Assertions.assertThat(deleted).isTrue();
    }

    @Test
    void deleteFailure_WithNoRoleInDB() {
        //given
        Mockito.doReturn(true).when(roleRepository).existsByRoleName(testRole.getRoleName());
        Mockito.doReturn(Optional.empty()).when(roleRepository).findByRoleName(testRole.getRoleName());

        //when
        boolean deleted = roleService.delete(testRole);

        //then
        Mockito.verify(roleRepository, Mockito.times(1)).existsByRoleName(testRole.getRoleName());
        Mockito.verify(roleRepository, Mockito.times(1)).findByRoleName(testRole.getRoleName());
        Mockito.verify(roleRepository, Mockito.times(0)).delete(testRole);
        Assertions.assertThat(deleted).isFalse();
    }

    @Test
    void deleteFailure_WithExistsByRoleNameFalse() {
        //when
        boolean deleted = roleService.delete(testRole);

        //then
        Mockito.verify(roleRepository, Mockito.times(1)).existsByRoleName(testRole.getRoleName());
        Mockito.verify(roleRepository, Mockito.times(0)).delete(testRole);
        Assertions.assertThat(deleted).isFalse();
    }

    @Test
    void convertToRoleStyle() {
        //given
        String expectedString = "TEST_STRING";

        //when
        String actualString = roleService.convertToRoleStyle("Test String");

        //then
        Assertions.assertThat(actualString).isEqualTo(expectedString);
    }
}