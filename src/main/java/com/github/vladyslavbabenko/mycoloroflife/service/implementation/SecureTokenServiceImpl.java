package com.github.vladyslavbabenko.mycoloroflife.service.implementation;

import com.github.vladyslavbabenko.mycoloroflife.entity.SecureToken;
import com.github.vladyslavbabenko.mycoloroflife.repository.SecureTokenRepository;
import com.github.vladyslavbabenko.mycoloroflife.service.SecureTokenService;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of {@link SecureTokenService}.
 */

@Service
public class SecureTokenServiceImpl implements SecureTokenService {
    @Value("${secure.token.validity}")
    private int tokenValidityInSeconds;

    private final SecureTokenRepository secureTokenRepository;

    @Autowired
    public SecureTokenServiceImpl(SecureTokenRepository secureTokenRepository) {
        this.secureTokenRepository = secureTokenRepository;
    }

    public int getTokenValidityInSeconds() {
        return tokenValidityInSeconds;
    }

    @Override
    public List<SecureToken> getAll() {
        List<SecureToken> secureTokens = secureTokenRepository.findAll();
        secureTokens.sort(Comparator.comparingInt(SecureToken::getId));
        return secureTokens;
    }

    @Override
    public Optional<SecureToken> findByToken(String token) {
        return secureTokenRepository.findByToken(token);
    }

    @Override
    public SecureToken createSecureToken() {
        String tokenValue = new String(Base64.encodeBase64URLSafe(KeyGenerators.secureRandom(15).generateKey()), StandardCharsets.UTF_8);
        SecureToken secureToken = SecureToken.builder()
                .token(tokenValue)
                .expireAt(LocalDateTime.now().plusSeconds(getTokenValidityInSeconds()))
                .build();
        save(secureToken);
        return secureToken;
    }

    @Override
    public boolean save(SecureToken token) {
        if (findByToken(token.getToken()).isEmpty()) {
            secureTokenRepository.save(token);
            deleteAllExpired();
            return true;
        } else return false;
    }

    @Override
    public boolean delete(SecureToken token) {
        if (findByToken(token.getToken()).isEmpty()) {
            return false;
        } else {
            secureTokenRepository.delete(token);
            return true;
        }
    }

    @Override
    public boolean delete(String token) {
        if (findByToken(token).isEmpty()) {
            return false;
        } else {
            secureTokenRepository.deleteByToken(token);
            return true;
        }
    }

    @Override
    public boolean deleteAllExpired() {
        List<SecureToken> secureTokens = getAll();
        if (secureTokens.isEmpty()) {
            return false;
        } else {
            secureTokens.stream().filter(SecureToken::isExpired).forEach(this::delete);
            return true;
        }
    }

    @Override
    public boolean update(SecureToken updatedToken) {
        Optional<SecureToken> tokenFromDB = findByToken(updatedToken.getToken());
        if (tokenFromDB.isEmpty()) {
            return false;
        } else {
            SecureToken tokenToUpdate = tokenFromDB.get();
            tokenToUpdate.setToken(updatedToken.getToken());
            tokenToUpdate.setUser(updatedToken.getUser());
            secureTokenRepository.save(tokenToUpdate);
            return true;
        }
    }
}