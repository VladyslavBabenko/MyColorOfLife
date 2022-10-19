package com.github.vladyslavbabenko.mycoloroflife.service.implementation;

import com.github.vladyslavbabenko.mycoloroflife.entity.User;
import com.github.vladyslavbabenko.mycoloroflife.service.BruteForceProtectionService;
import com.github.vladyslavbabenko.mycoloroflife.service.UserService;
import org.fest.assertions.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

@DisplayName("Unit-level testing for BruteForceProtectionService")
class BruteForceProtectionServiceImplTest {

    private BruteForceProtectionServiceImpl bruteForceProtectionService;
    private UserService userService;
    private User expectedUser;
    private final int maxFailedLogins = 10;

    @BeforeEach
    void setUp() {
        //given
        userService = Mockito.mock(UserService.class);

        bruteForceProtectionService = new BruteForceProtectionServiceImpl(userService);

        expectedUser = User.builder()
                .id(1)
                .username("TestUser")
                .email("TestUser@mail.com")
                .failedLoginAttempt(0)
                .isAccountNonLocked(true)
                .build();
    }

    @Test
    void isBruteForceProtectionServiceImplTestReady() {
        Assertions.assertThat(bruteForceProtectionService).isNotNull().isInstanceOf(BruteForceProtectionService.class);
        Assertions.assertThat(userService).isNotNull().isInstanceOf(UserService.class);
        Assertions.assertThat(expectedUser).isNotNull().isInstanceOf(User.class);
    }

    @Test
    void registerLoginFailure() {
        //given
        Mockito.doReturn(expectedUser).when(userService).loadUserByUsername(expectedUser.getUsername());

        //when
        bruteForceProtectionService.registerLoginFailure(expectedUser.getUsername());

        //then
        Mockito.verify(userService, Mockito.times(1)).loadUserByUsername(expectedUser.getUsername());
        Mockito.verify(userService, Mockito.times(1)).updateUser(expectedUser);
    }

    @Test
    void resetBruteForceCounter() {
        //given
        Mockito.doReturn(expectedUser).when(userService).loadUserByUsername(expectedUser.getUsername());

        //when
        bruteForceProtectionService.resetBruteForceCounter(expectedUser.getUsername());

        //then
        Mockito.verify(userService, Mockito.times(1)).updateUser(expectedUser);
    }

    @Test
    void isBruteForceAttackTrue() {
        //given
        Mockito.doReturn(expectedUser).when(userService).loadUserByUsername(expectedUser.getUsername());

        //when
        boolean isAttack = bruteForceProtectionService.isBruteForceAttack(expectedUser.getUsername());

        //then
        Assertions.assertThat(isAttack).isTrue();
    }

    @Test
    void isBruteForceAttackFalse() {
        //given
        Mockito.doReturn(null).when(userService).loadUserByUsername(expectedUser.getUsername());

        //when
        boolean isAttack = bruteForceProtectionService.isBruteForceAttack(expectedUser.getUsername());

        //then
        Assertions.assertThat(isAttack).isFalse();
    }

    @Test
    void getMaxFailedLogins() {
        //when
        int num = bruteForceProtectionService.getMaxFailedLogins();

        //then
        Assertions.assertThat(num).isZero();
    }
}