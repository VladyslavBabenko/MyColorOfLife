package com.github.vladyslavbabenko.mycoloroflife.repository;

import com.github.vladyslavbabenko.mycoloroflife.entity.SecureToken;
import com.github.vladyslavbabenko.mycoloroflife.entity.User;
import com.github.vladyslavbabenko.mycoloroflife.enumeration.Purpose;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * {@link Repository} for handling with {@link SecureToken} entity.
 */

@Repository
public interface SecureTokenRepository extends JpaRepository<SecureToken, Integer> {
    /**
     * Finds {@link SecureToken} by token.
     *
     * @param token token to search
     * @return Optional {@link SecureToken} from database, otherwise empty Optional
     */
    Optional<SecureToken> findByToken(String token);

    /**
     * Deletes {@link SecureToken} by token
     *
     * @param token token to delete
     */
    void deleteByToken(String token);

    /**
     * Checks that exists a {@link SecureToken} for provided {@link User} and with {@link Purpose}
     *
     * @param user    user to search
     * @param purpose purpose to search
     * @return true if {@link SecureToken} exists by provided user and purpose, otherwise false
     */
    boolean existsByUserAndPurpose(User user, Purpose purpose);
}