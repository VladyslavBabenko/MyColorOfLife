package com.github.vladyslavbabenko.mycoloroflife.service.implementation;

import com.github.vladyslavbabenko.mycoloroflife.AbstractTest.AbstractTest;
import com.github.vladyslavbabenko.mycoloroflife.entity.*;
import com.github.vladyslavbabenko.mycoloroflife.enumeration.UserRegistrationType;
import com.github.vladyslavbabenko.mycoloroflife.repository.UserRepository;
import com.github.vladyslavbabenko.mycoloroflife.service.*;
import org.fest.assertions.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Unit-level testing for UserService")
class UserServiceImplTest extends AbstractTest {

    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private UserRepository userRepository;
    private UserService userService;
    private OAuth2UserAuthority oAuth2UserAuthorityMock;
    private ActivationCodeService activationCodeService;
    private RoleService roleService;
    private CourseProgressService courseProgressService;
    private CourseService courseService;

    private User testUser, testUserGAuth;
    private Role testRole;
    private ActivationCode testActivationCode;
    private Course testCourse;
    private CourseTitle testCourseTitle;

    @BeforeEach
    void setUp() {
        //given
        userRepository = Mockito.mock(UserRepository.class);
        bCryptPasswordEncoder = Mockito.mock(BCryptPasswordEncoder.class);
        activationCodeService = Mockito.mock(ActivationCodeService.class);
        roleService = Mockito.mock(RoleService.class);
        courseProgressService = Mockito.mock(CourseProgressService.class);
        courseService = Mockito.mock(CourseService.class);

        userService = new UserServiceImpl(userRepository, bCryptPasswordEncoder, activationCodeService, roleService, courseProgressService, courseService);

        oAuth2UserAuthorityMock = Mockito.mock(OAuth2UserAuthority.class);

        Set<Role> roles = new HashSet<>();
        roles.add(testRole);

        testUser = User.builder()
                .id(1)
                .name("TestUser")
                .email("testuser@mail.com")
                .roles(roles)
                .password(String.valueOf(123456789))
                .registrationType(UserRegistrationType.REGISTRATION_FORM)
                .build();

        testUserGAuth = User.builder()
                .id(4)
                .name("TestUserGAuth")
                .email("testusergauth@gmail.com")
                .roles(roles)
                .registrationType(UserRegistrationType.GMAIL_AUTHENTICATION)
                .build();

        testRole = Role.builder().id(1).roleName("ROLE_USER").build();

        testCourseTitle = CourseTitle.builder().id(1).title("test").build();

        testActivationCode = ActivationCode.builder().id(1).code("qqqqqwwwwweeeee").courseTitle(testCourseTitle).user(testUser).build();

        testCourse = Course.builder().id(1).courseTitle(testCourseTitle).text("test text").build();

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("name", testUserGAuth.getName());
        attributes.put("email", testUserGAuth.getEmail());

        oAuth2UserAuthorityMock = new OAuth2UserAuthority(attributes);
    }

    @Test
    void isUserServiceImplTestReady() {
        Assertions.assertThat(bCryptPasswordEncoder).isNotNull().isInstanceOf(BCryptPasswordEncoder.class);
        Assertions.assertThat(userRepository).isNotNull().isInstanceOf(UserRepository.class);
        Assertions.assertThat(userService).isNotNull().isInstanceOf(UserServiceImpl.class);
        Assertions.assertThat(testUser).isNotNull().isInstanceOf(User.class);
        Assertions.assertThat(testUserGAuth).isNotNull().isInstanceOf(User.class);
        Assertions.assertThat(testRole).isNotNull().isInstanceOf(Role.class);
        Assertions.assertThat(testCourseTitle).isNotNull().isInstanceOf(CourseTitle.class);
        Assertions.assertThat(testActivationCode).isNotNull().isInstanceOf(ActivationCode.class);
        Assertions.assertThat(testCourse).isNotNull().isInstanceOf(Course.class);
    }

    @Test
    void shouldFindUserById() {
        //when
        userService.findById(testUser.getId());

        //then
        Mockito.verify(userRepository, Mockito.times(1)).findById(testUser.getId());
    }

    @Test
    void shouldReturnSortedListOfUsers() {
        //when
        userService.getAllUsers();

        //then
        Mockito.verify(userRepository, Mockito.times(1)).findAll();
        Assertions.assertThat(userService.getAllUsers())
                .isNotNull()
                .isInstanceOf(List.class)
                .isSorted();
    }

    @Test
    void shouldReturnEncodedPassword() {
        //when
        userService.encodePassword(testUser.getPassword());

        //then
        Mockito.verify(bCryptPasswordEncoder, Mockito.times(1)).encode(testUser.getPassword());
    }

    @Test
    void shouldReturnNullBecauseOfBlankInput() {
        //when
        String result = userService.encodePassword("");

        //then
        Mockito.verify(bCryptPasswordEncoder, Mockito.times(0)).encode("");
        Assertions.assertThat(result).isNullOrEmpty();
    }

    @Test
    void shouldSaveUser() {
        //when
        boolean isUserSaved = userService.saveUser(testUser);

        //then
        Mockito.verify(userRepository, Mockito.times(1)).save(testUser);
        Assertions.assertThat(testUser.getRoles()).isEqualTo(Collections.singleton(Role.builder().id(1).roleName("ROLE_USER").build()));
        Assertions.assertThat(isUserSaved).isTrue();
    }

    @Test
    void shouldNotSaveUserByEmail() {
        //when
        testUser.setId(null);
        Mockito.doReturn(Optional.ofNullable(testUser))
                .when(userRepository)
                .findByEmail(testUser.getEmail());

        boolean isUserCreated = userService.saveUser(testUser);

        //then
        Mockito.verify(userRepository, Mockito.times(0)).save(testUser);
        Assertions.assertThat(isUserCreated).isFalse();
    }

    @Test
    void shouldNotDeleteUser() {
        //when
        boolean isUserDeleted = userService.deleteUser(testUser.getId());

        //then
        Mockito.verify(userRepository, Mockito.times(1)).existsById(testUser.getId());
        Mockito.verify(userRepository, Mockito.times(0)).deleteById(testUser.getId());
        Assertions.assertThat(isUserDeleted).isFalse();
    }

    @Test
    void shouldDeleteUser() {
        //when
        Mockito.doReturn(true).when(userRepository).existsById(testUser.getId());
        boolean isUserDeleted = userService.deleteUser(testUser.getId());

        //then
        Mockito.verify(userRepository, Mockito.times(1)).existsById(testUser.getId());
        Mockito.verify(userRepository, Mockito.times(1)).deleteById(testUser.getId());
        Assertions.assertThat(isUserDeleted).isTrue();
    }

    @Test
    void shouldUpdateUser() {
        //when
        Mockito.doReturn(Optional.of(testUser)).when(userRepository).findById(testUser.getId());
        boolean isUserUpdated = userService.updateUser(testUser);

        //then
        Mockito.verify(userRepository, Mockito.times(1)).save(testUser);
        Assertions.assertThat(isUserUpdated).isTrue();
    }

    @Test
    void shouldNotUpdateUser() {
        //when
        Mockito.doReturn(Optional.empty()).when(userRepository).findById(testUser.getId());
        boolean isUserUpdated = userService.updateUser(testUser);

        //then
        Mockito.verify(userRepository, Mockito.times(0)).save(testUser);
        Assertions.assertThat(isUserUpdated).isFalse();
    }

    @Test
    void shouldChangePassword() {
        //when
        Mockito.doReturn(Optional.of(testUser)).when(userRepository).findById(testUser.getId());
        boolean isPasswordChanged = userService.changePassword(testUser);
        //then
        Mockito.verify(userRepository, Mockito.times(1)).save(testUser);
        Assertions.assertThat(isPasswordChanged).isTrue();
    }

    @Test
    void shouldNotChangePassword() {
        //when
        Mockito.doReturn(Optional.empty()).when(userRepository).findById(testUser.getId());
        boolean isPasswordChanged = userService.changePassword(testUser);
        //then
        Mockito.verify(userRepository, Mockito.times(0)).save(testUser);
        Assertions.assertThat(isPasswordChanged).isFalse();
    }

    @Test
    void shouldLoadUserByEmail() {
        //when
        Mockito.doReturn(Optional.ofNullable(testUser)).when(userRepository).findByEmail(testUser.getEmail());
        UserDetails userFromMethod = userService.loadUserByUsername(testUser.getEmail());
        //then
        Assertions.assertThat(userFromMethod).isNotNull().isEqualTo(testUser);
    }

    @Test
    void shouldNotLoadUserByUsername() {
        //when
        Mockito.doReturn(Optional.empty()).when(userRepository).findByEmail(testUser.getUsername());

        Exception exception =
                assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername(testUser.getUsername()));

        String expectedMessage = "User with " + testUser.getUsername() + " not found";
        String actualMessage = exception.getMessage();

        Assertions.assertThat(actualMessage).isNotNull().isNotEmpty().isEqualTo(expectedMessage);
    }

    @Test
    void shouldSaveOAuth2User() {
        boolean saved = userService.saveOAuth2User(oAuth2UserAuthorityMock);

        Assertions.assertThat(testUserGAuth).isNotNull().isInstanceOf(User.class).isEqualTo(testUserGAuth);
        Assertions.assertThat(saved).isTrue();
    }

    @Test
    void deleteRoleFromUser() {
        //given
        Mockito.doReturn(true).when(roleService).existsByRoleName(testRole.getRoleName());

        List<User> users = new ArrayList<>();
        users.add(testUser);
        users.add(testUserGAuth);

        //when
        boolean isTrue = userService.deleteRoleFromUser(users, testRole);

        //Then
        Mockito.verify(roleService, Mockito.times(1)).existsByRoleName(testRole.getRoleName());
        Assertions.assertThat(isTrue).isTrue();
    }

    @Test
    void deleteRoleFromUserFailure() {
        //given
        List<User> users = new ArrayList<>();
        users.add(testUser);
        users.add(testUserGAuth);

        //when
        boolean isFalse = userService.deleteRoleFromUser(users, testRole);

        //Then
        Mockito.verify(roleService, Mockito.times(1)).existsByRoleName(testRole.getRoleName());
        Assertions.assertThat(isFalse).isFalse();
    }

    @Test
    void activateCodeFailure() {
        //when
        boolean isFalse = userService.activateCode(testActivationCode);

        //then
        Assertions.assertThat(isFalse).isFalse();
    }

    @Test
    void activateCodeSuccess() {
        //given
        String courseOwnerRoleAsString = "ROLE_COURSE_OWNER_" + testActivationCode.getCourseTitle().getTitle().toUpperCase(Locale.ROOT);

        Mockito.doReturn(true).when(activationCodeService).existsByCode(testActivationCode.getCode());
        Mockito.doReturn(testActivationCode.getCourseTitle().getTitle().toUpperCase(Locale.ROOT)).when(roleService).convertToRoleStyle(testActivationCode.getCourseTitle().getTitle());
        Mockito.doReturn(true).when(roleService).existsByRoleName(courseOwnerRoleAsString);
        Mockito.doReturn(Optional.of(testRole)).when(roleService).findByRoleName(courseOwnerRoleAsString);
        Mockito.doReturn(Optional.of(testCourse)).when(courseService).findByCourseTitleAndPage(testActivationCode.getCourseTitle().getTitle(), 1);

        //when
        boolean isTrue = userService.activateCode(testActivationCode);

        //then
        Mockito.verify(activationCodeService, Mockito.times(1)).existsByCode(testActivationCode.getCode());
        Mockito.verify(roleService, Mockito.times(1)).convertToRoleStyle(testActivationCode.getCourseTitle().getTitle());
        Mockito.verify(roleService, Mockito.times(1)).existsByRoleName(courseOwnerRoleAsString);
        Mockito.verify(userRepository, Mockito.times(1)).save(testActivationCode.getUser());
        Mockito.verify(activationCodeService, Mockito.times(1)).deleteByCode(testActivationCode.getCode());
        Mockito.verify(courseService, Mockito.times(1)).findByCourseTitleAndPage(testActivationCode.getCourseTitle().getTitle(), 1);
        Assertions.assertThat(isTrue).isTrue();
    }

    @Test
    void existsById() {
        //when
        userService.existsById(testUser.getId());

        //then
        Mockito.verify(userRepository, Mockito.times(1)).existsById(testUser.getId());
    }

    @Test
    void isAccountNonLocked() {
        //given
        Mockito.doReturn(Optional.ofNullable(testUser)).when(userRepository).findByEmail(testUser.getUsername());

        //when
        userService.isAccountNonLocked(testUser.getUsername());

        //then
        Mockito.verify(userRepository, Mockito.times(1)).findByEmail(testUser.getUsername());
    }
}