package com.github.vladyslavbabenko.mycoloroflife.repository;

import com.github.vladyslavbabenko.mycoloroflife.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * {@link Repository} for handling with {@link User} entity.
 */

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    /**
     * Finds a {@link User} by username. (username == email)
     *
     *
     * @param email email to search
     * @return Optional user from database, otherwise empty Optional
     */
    Optional<User> findByEmail(String email);
}