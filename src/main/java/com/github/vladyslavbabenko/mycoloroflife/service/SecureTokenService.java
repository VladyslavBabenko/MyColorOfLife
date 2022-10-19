package com.github.vladyslavbabenko.mycoloroflife.service;

import com.github.vladyslavbabenko.mycoloroflife.entity.SecureToken;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * {@link Service} for handling with {@link SecureToken} entity.
 */

public interface SecureTokenService {

    List<SecureToken> getAll();

    Optional<SecureToken> findByToken(String token);

    SecureToken createSecureToken();

    boolean save(SecureToken token);

    boolean delete(SecureToken token);

    boolean delete(String token);

    boolean deleteAllExpired();

    boolean update(SecureToken token);
}