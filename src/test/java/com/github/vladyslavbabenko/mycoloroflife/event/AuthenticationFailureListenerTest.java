package com.github.vladyslavbabenko.mycoloroflife.event;

import com.github.vladyslavbabenko.mycoloroflife.entity.Role;
import com.github.vladyslavbabenko.mycoloroflife.entity.User;
import com.github.vladyslavbabenko.mycoloroflife.enumeration.UserRegistrationType;
import com.github.vladyslavbabenko.mycoloroflife.service.BruteForceProtectionService;
import com.github.vladyslavbabenko.mycoloroflife.service.PasswordRecoveryService;
import org.fest.assertions.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.core.Authentication;

import java.util.HashSet;
import java.util.Set;

@DisplayName("Unit-level testing for AuthenticationFailureListener")
class AuthenticationFailureListenerTest {

    private BruteForceProtectionService bruteForceProtectionService;
    private PasswordRecoveryService passwordRecoveryService;
    private AuthenticationFailureListener authenticationFailureListener;
    private AuthenticationFailureBadCredentialsEvent event;
    private Authentication authentication;
    private User testUser;
    private Role testRole;
    private String msg;

    @BeforeEach
    void setUp() {
        //given
        msg = "Invalid user credentials";

        testRole = Role.builder().id(1).roleName("ROLE_USER").build();

        Set<Role> roles = new HashSet<>();
        roles.add(testRole);

        testUser = User.builder()
                .id(1)
                .username("TestUser")
                .email("TestUser@mail.com")
                .roles(roles)
                .password(String.valueOf(123456))
                .registrationType(UserRegistrationType.REGISTRATION_FORM)
                .build();

        authentication = new TestingAuthenticationToken(testUser, testUser.getPassword());
        event = new AuthenticationFailureBadCredentialsEvent(authentication, new BadCredentialsException(msg));

        bruteForceProtectionService = Mockito.mock(BruteForceProtectionService.class);
        passwordRecoveryService = Mockito.mock(PasswordRecoveryService.class);

        authenticationFailureListener = new AuthenticationFailureListener(bruteForceProtectionService, passwordRecoveryService);
    }

    @Test
    void isAuthenticationSuccessListenerTestReady() {
        Assertions.assertThat(bruteForceProtectionService).isNotNull().isInstanceOf(BruteForceProtectionService.class);
        Assertions.assertThat(authenticationFailureListener).isNotNull().isInstanceOf(AuthenticationFailureListener.class);
    }

    @Test
    void onApplicationEvent_WithBruteForceAttackFalse() {
        //given
        String username = event.getAuthentication().getName();

        //when
        authenticationFailureListener.onApplicationEvent(event);

        //then
        Mockito.verify(bruteForceProtectionService, Mockito.times(1)).registerLoginFailure(username);
        Mockito.verify(bruteForceProtectionService, Mockito.times(1)).isBruteForceAttack(username);
        Mockito.verify(passwordRecoveryService, Mockito.times(0)).loginDisabled(username);
    }

    @Test
    void onApplicationEvent_WithBruteForceAttackTrue() {
        //given
        String username = event.getAuthentication().getName();
        Mockito.doReturn(true).when(bruteForceProtectionService).isBruteForceAttack(username);

        //when
        authenticationFailureListener.onApplicationEvent(event);

        //then
        Mockito.verify(bruteForceProtectionService, Mockito.times(1)).registerLoginFailure(username);
        Mockito.verify(bruteForceProtectionService, Mockito.times(1)).isBruteForceAttack(username);
        Mockito.verify(passwordRecoveryService, Mockito.times(1)).loginDisabled(username);
    }
}