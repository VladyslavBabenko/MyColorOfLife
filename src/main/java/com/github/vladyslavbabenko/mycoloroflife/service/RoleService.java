package com.github.vladyslavbabenko.mycoloroflife.service;

import com.github.vladyslavbabenko.mycoloroflife.entity.Role;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * {@link Service} for handling with {@link Role} entity.
 */

public interface RoleService {
    /**
     * Find {@link Role} by roleId.
     *
     * @param roleId provided role id.
     * @return Optional Role from database
     */
    Optional<Role> findById(Integer roleId);

    /**
     * Get all {@link Role} entities.
     *
     * @return the collection of existing {@link Role} objects.
     */
    List<Role> getAllRoles();

    /**
     * Find {@link Role} by roleName.
     *
     * @param roleName provided role name.
     * @return Optional Role from database
     */
    Optional<Role> findByRoleName(String roleName);

    /**
     * Find {@link Role} by roleName.
     *
     * @param roleName provided role name.
     * @return true if {@link Role} exists, false otherwise.
     */
    boolean existsByRoleName(String roleName);

    /**
     * Saves provided role into database
     *
     * @param roleToSave provided {@link Role} to save
     * @return true if operation was successful, otherwise false.
     */
    boolean save(Role roleToSave);

    /**
     * Updates provided {@link Role}
     *
     * @param updatedRole provided {@link Role} to update
     * @return true if operation was successful, otherwise false.
     */
    boolean update(Role updatedRole);

    /**
     * Updates provided {@link Role}
     *
     * @param roleToUpdate {@link Role} what will be updated
     * @param updatedRole provided updated {@link Role}
     * @return true if operation was successful, otherwise false.
     */
    boolean update(Role roleToUpdate, Role updatedRole);

    /**
     * Deletes {@link Role} from database
     *
     * @param roleToDelete {@link Role} to delete from database
     * @return true if operation was successful, otherwise false.
     */
    boolean delete(Role roleToDelete);

    /**
     * Converts provided string into role style spelling
     *
     * @param string string to convert
     * @return Converted string into role style spelling
     */
    String convertToRoleStyle(String string);
}