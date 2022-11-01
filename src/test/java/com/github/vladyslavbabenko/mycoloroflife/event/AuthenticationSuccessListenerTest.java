package com.github.vladyslavbabenko.mycoloroflife.event;

import com.github.vladyslavbabenko.mycoloroflife.entity.Role;
import com.github.vladyslavbabenko.mycoloroflife.entity.User;
import com.github.vladyslavbabenko.mycoloroflife.enumeration.UserRegistrationType;
import com.github.vladyslavbabenko.mycoloroflife.service.BruteForceProtectionService;
import org.fest.assertions.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;

import java.util.HashSet;
import java.util.Set;

@DisplayName("Unit-level testing for AuthenticationSuccessListener")
class AuthenticationSuccessListenerTest {

    private BruteForceProtectionService bruteForceProtectionService;
    private AuthenticationSuccessListener authenticationSuccessListener;
    private AuthenticationSuccessEvent event;
    private Authentication authentication;
    private User testUser;
    private Role testRole;

    @BeforeEach
    void setUp() {
        //given
        testRole = Role.builder().id(1).roleName("ROLE_USER").build();

        Set<Role> roles = new HashSet<>();
        roles.add(testRole);

        testUser = User.builder()
                .id(1)
                .name("TestUser")
                .email("TestUser@mail.com")
                .roles(roles)
                .password(String.valueOf(123456))
                .registrationType(UserRegistrationType.REGISTRATION_FORM)
                .build();

        authentication = new TestingAuthenticationToken(testUser, testUser.getPassword());
        event = new AuthenticationSuccessEvent(authentication);

        bruteForceProtectionService = Mockito.mock(BruteForceProtectionService.class);
        authenticationSuccessListener = new AuthenticationSuccessListener(bruteForceProtectionService);
    }

    @Test
    void isAuthenticationSuccessListenerTestReady() {
        Assertions.assertThat(bruteForceProtectionService).isNotNull().isInstanceOf(BruteForceProtectionService.class);
        Assertions.assertThat(authenticationSuccessListener).isNotNull().isInstanceOf(AuthenticationSuccessListener.class);
    }

    @Test
    void onApplicationEvent() {
        //when
        authenticationSuccessListener.onApplicationEvent(event);

        //then
        Mockito.verify(bruteForceProtectionService, Mockito.times(1)).resetBruteForceCounter(((User) event.getAuthentication().getPrincipal()).getUsername());
    }
}