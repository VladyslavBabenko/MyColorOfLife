package com.github.vladyslavbabenko.mycoloroflife.repository;

import com.github.vladyslavbabenko.mycoloroflife.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * {@link Repository} for handling with {@link User} entity.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    /**
     * Finds a user by username.
     *
     * @param username username to search
     * @return If present, returns the user from the database, otherwise null
     */
    User findByUsername(String username);

    /**
     * Finds a user by  email.
     *
     * @param email email to search
     * @return If present, returns the user from the database, otherwise null
     */
    User findByEmail(String email);
}