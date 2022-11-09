package com.github.vladyslavbabenko.mycoloroflife.service.implementation;

import com.github.vladyslavbabenko.mycoloroflife.AbstractTest.AbstractTest;
import com.github.vladyslavbabenko.mycoloroflife.entity.SecureToken;
import com.github.vladyslavbabenko.mycoloroflife.entity.User;
import com.github.vladyslavbabenko.mycoloroflife.enumeration.Purpose;
import com.github.vladyslavbabenko.mycoloroflife.repository.SecureTokenRepository;
import com.github.vladyslavbabenko.mycoloroflife.service.SecureTokenService;
import org.fest.assertions.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.util.ReflectionTestUtils;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@DisplayName("Unit-level testing for SecureTokenService")
class SecureTokenServiceImplTest extends AbstractTest {

    private SecureTokenServiceImpl secureTokenService;
    private SecureTokenRepository secureTokenRepository;
    private SecureToken expectedSecureToken;
    private User expectedUser;

    @Value("${secure.token.validity}")
    private int tokenValidityInSeconds;

    @BeforeEach
    void setUp() {
        //given
        secureTokenRepository = Mockito.mock(SecureTokenRepository.class);

        secureTokenService = new SecureTokenServiceImpl(secureTokenRepository);

        expectedUser = User.builder()
                .id(1)
                .name("TestUser")
                .email("TestUser@mail.com")
                .build();

        ReflectionTestUtils.setField(secureTokenService, "tokenValidityInSeconds", tokenValidityInSeconds);

        expectedSecureToken = SecureToken.builder().id(1).token("wMQzFUNrjsXyyht0lF-B").timeStamp(Timestamp.valueOf(LocalDateTime.now())).expireAt(LocalDateTime.now().plusSeconds(tokenValidityInSeconds)).user(expectedUser).build();
    }

    @Test
    void isSecureTokenServiceImplTestReady() {
        Assertions.assertThat(secureTokenRepository).isNotNull().isInstanceOf(SecureTokenRepository.class);
        Assertions.assertThat(secureTokenService).isNotNull().isInstanceOf(SecureTokenService.class);
        Assertions.assertThat(expectedUser).isNotNull().isInstanceOf(User.class);
        Assertions.assertThat(expectedSecureToken).isNotNull().isInstanceOf(SecureToken.class);
    }

    @Test
    void getTokenValidityInSeconds() {
        //given
        int actualTokenValidityInSeconds = secureTokenService.getTokenValidityInSeconds();

        //then
        Assertions.assertThat(actualTokenValidityInSeconds).isEqualTo(tokenValidityInSeconds);
    }

    @Test
    void getAll() {
        //when
        List<SecureToken> secureTokens = secureTokenService.getAll();

        //then
        Mockito.verify(secureTokenRepository, Mockito.times(1)).findAll();
        Assertions.assertThat(secureTokens)
                .isNotNull()
                .isInstanceOf(List.class)
                .isSorted();
    }

    @Test
    void findByToken() {
        Mockito.doReturn(Optional.ofNullable(expectedSecureToken)).when(secureTokenRepository).findByToken(expectedSecureToken.getToken());
        //when
        Optional<SecureToken> optionalSecureToken = secureTokenService.findByToken(expectedSecureToken.getToken());

        //then
        Mockito.verify(secureTokenRepository, Mockito.times(1)).findByToken(expectedSecureToken.getToken());
        Assertions.assertThat(optionalSecureToken.get()).isEqualTo(expectedSecureToken);
    }

    @Test
    void createSecureToken() {
        //when
        SecureToken token = secureTokenService.createSecureToken();

        //then
        Mockito.verify(secureTokenRepository, Mockito.times(1)).save(token);
        Assertions.assertThat(token).isInstanceOf(SecureToken.class);
    }

    @Test
    void saveSuccess() {
        //when
        boolean isSaved = secureTokenService.save(expectedSecureToken);

        //then
        Assertions.assertThat(isSaved).isTrue();
    }

    @Test
    void saveFailure() {
        //given
        Mockito.doReturn(Optional.ofNullable(expectedSecureToken)).when(secureTokenRepository).findByToken(expectedSecureToken.getToken());

        //when
        boolean isSaved = secureTokenService.save(expectedSecureToken);

        //then
        Assertions.assertThat(isSaved).isFalse();
    }

    @Test
    void deleteWithSecureTokenArgSuccess() {
        //given
        Mockito.doReturn(Optional.ofNullable(expectedSecureToken)).when(secureTokenRepository).findByToken(expectedSecureToken.getToken());

        //when
        boolean isDeleted = secureTokenService.delete(expectedSecureToken);

        //then
        Assertions.assertThat(isDeleted).isTrue();
    }

    @Test
    void deleteWithSecureTokenArgFailure() {
        //when
        boolean isDeleted = secureTokenService.delete(expectedSecureToken);

        //then
        Assertions.assertThat(isDeleted).isFalse();
    }

    @Test
    void deleteWithStringArgSuccess() {
        //given
        Mockito.doReturn(Optional.ofNullable(expectedSecureToken)).when(secureTokenRepository).findByToken(expectedSecureToken.getToken());

        //when
        boolean isDeleted = secureTokenService.delete(expectedSecureToken.getToken());

        //then
        Assertions.assertThat(isDeleted).isTrue();
    }

    @Test
    void deleteWithStringArgFailure() {
        //when
        boolean deleted = secureTokenService.delete(expectedSecureToken.getToken());

        //then
        Assertions.assertThat(deleted).isFalse();
    }

    @Test
    void deleteAllExpiredSuccess() {
        //given
        List<SecureToken> tokens = new ArrayList<>();
        tokens.add(expectedSecureToken);
        Mockito.doReturn(tokens).when(secureTokenRepository).findAll();

        //when
        boolean isDeletedAllExpired = secureTokenService.deleteAllExpired();

        //then
        Mockito.verify(secureTokenRepository, Mockito.times(1)).findAll();
        Assertions.assertThat(isDeletedAllExpired).isTrue();
    }

    @Test
    void deleteAllExpiredFailure() {
        //when
        boolean isDeletedAllExpired = secureTokenService.deleteAllExpired();

        //then
        Mockito.verify(secureTokenRepository, Mockito.times(1)).findAll();
        Assertions.assertThat(isDeletedAllExpired).isFalse();
    }

    @Test
    void updateSuccess() {
        //given
        Mockito.doReturn(Optional.ofNullable(expectedSecureToken)).when(secureTokenRepository).findByToken(expectedSecureToken.getToken());

        //when
        boolean isUpdated = secureTokenService.update(expectedSecureToken);

        //then
        Mockito.verify(secureTokenRepository, Mockito.times(1)).save(expectedSecureToken);
        Assertions.assertThat(isUpdated).isTrue();
    }

    @Test
    void updateFailure() {
        //when
        boolean isUpdated = secureTokenService.update(expectedSecureToken);

        //then
        Mockito.verify(secureTokenRepository, Mockito.times(0)).save(expectedSecureToken);
        Assertions.assertThat(isUpdated).isFalse();
    }

    @Test
    void existsByUserAndPurposeTrue() {
        //given
        Mockito.doReturn(true).when(secureTokenRepository).existsByUserAndPurpose(expectedUser, Purpose.PASSWORD_RECOVERY);

        //when
        boolean exists = secureTokenService.existsByUserAndPurpose(expectedUser, Purpose.PASSWORD_RECOVERY);

        //then
        Mockito.verify(secureTokenRepository, Mockito.times(1)).existsByUserAndPurpose(expectedUser, Purpose.PASSWORD_RECOVERY);
        Assertions.assertThat(exists).isTrue();
    }

    @Test
    void existsByUserAndPurposeFalse() {
        //when
        boolean exists = secureTokenService.existsByUserAndPurpose(expectedUser, Purpose.PASSWORD_RECOVERY);

        //then
        Mockito.verify(secureTokenRepository, Mockito.times(1)).existsByUserAndPurpose(expectedUser, Purpose.PASSWORD_RECOVERY);
        Assertions.assertThat(exists).isFalse();
    }
}