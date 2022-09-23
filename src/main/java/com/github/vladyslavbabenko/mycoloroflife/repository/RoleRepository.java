package com.github.vladyslavbabenko.mycoloroflife.repository;

import com.github.vladyslavbabenko.mycoloroflife.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * {@link Repository} for handling with {@link Role} entity.
 */

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    /**
     * Finds all {@link Role} by roleName.
     *
     * @param roleName roleName to search
     * @return Optional Role from database, otherwise empty Optional
     */
    Optional<Role> findByRoleName(String roleName);

    /**
     * Finds an {@link Role} by roleName.
     *
     * @param roleName roleName to search
     * @return true if exists, otherwise false
     */
    boolean existsByRoleName(String roleName);
}