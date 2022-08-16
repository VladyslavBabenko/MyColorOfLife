package com.github.vladyslavbabenko.mycoloroflife.service;

import com.github.vladyslavbabenko.mycoloroflife.entity.Role;
import com.github.vladyslavbabenko.mycoloroflife.entity.User;
import com.github.vladyslavbabenko.mycoloroflife.enumeration.UserRegistrationType;
import com.github.vladyslavbabenko.mycoloroflife.repository.UserRepository;
import org.fest.assertions.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Unit-level testing for UserService")
class UserServiceImplTest {
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private UserRepository userRepository;
    private UserService userService;
    private User testUser, testUserGAuth;
    private OAuth2UserAuthority oAuth2UserAuthorityMock;

    @BeforeEach
    void setUp() {
        //given
        userRepository = Mockito.mock(UserRepository.class);
        bCryptPasswordEncoder = Mockito.mock(BCryptPasswordEncoder.class);
        userService = new UserServiceImpl(userRepository, bCryptPasswordEncoder);

        oAuth2UserAuthorityMock = Mockito.mock(OAuth2UserAuthority.class);

        testUser = User.builder()
                .id(1)
                .username("TestUser")
                .email("TestMail@mail.com")
                .password(String.valueOf(123456789))
                .registrationType(UserRegistrationType.REGISTRATION_FORM)
                .build();

        testUserGAuth = User.builder()
                .id(4)
                .username("TestUserGAuth")
                .email("TestUserGAuth@gmail.com")
                .registrationType(UserRegistrationType.GMAIL_AUTHENTICATION)
                .build();
    }

    @Test
    void isUserServiceImplTestReady() {
        Assertions.assertThat(bCryptPasswordEncoder).isNotNull().isInstanceOf(BCryptPasswordEncoder.class);
        Assertions.assertThat(userRepository).isNotNull().isInstanceOf(UserRepository.class);
        Assertions.assertThat(userService).isNotNull().isInstanceOf(UserServiceImpl.class);
        Assertions.assertThat(testUser).isNotNull().isInstanceOf(User.class);
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
    void shouldNotSaveUserById() {
        //when
        Mockito.doReturn(Optional.of(testUser))
                .when(userRepository)
                .findById(testUser.getId());

        boolean isUserCreated = userService.saveUser(testUser);

        //then
        Mockito.verify(userRepository, Mockito.times(0)).save(testUser);
        Assertions.assertThat(isUserCreated).isFalse();
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
        Mockito.doReturn(Optional.empty()).when(userRepository).findByUsername(testUser.getUsername());

        Exception exception =
                assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername(testUser.getUsername()));

        String expectedMessage = "User with " + testUser.getUsername() + " not found";
        String actualMessage = exception.getMessage();

        Assertions.assertThat(actualMessage).isNotNull().isNotEmpty().isEqualTo(expectedMessage);
    }

    @Test
    void shouldSaveOAuth2User() {
        userService.saveOAuth2User(oAuth2UserAuthorityMock);

        Assertions.assertThat(testUserGAuth).isNotNull().isInstanceOf(User.class).isEqualTo(testUserGAuth);
    }
}