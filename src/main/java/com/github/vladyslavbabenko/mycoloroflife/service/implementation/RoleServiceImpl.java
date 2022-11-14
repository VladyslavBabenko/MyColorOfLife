package com.github.vladyslavbabenko.mycoloroflife.service.implementation;

import com.github.vladyslavbabenko.mycoloroflife.entity.Role;
import com.github.vladyslavbabenko.mycoloroflife.repository.RoleRepository;
import com.github.vladyslavbabenko.mycoloroflife.service.RoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Implementation of {@link RoleService}.
 */

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Optional<Role> findById(Integer roleId) {
        return roleRepository.findById(roleId);
    }

    @Override
    public List<Role> getAllRoles() {
        List<Role> roles = roleRepository.findAll();
        roles.sort(Comparator.comparingInt(Role::getId));
        return roles;
    }

    @Override
    public Optional<Role> findByRoleName(String roleName) {
        return roleRepository.findByRoleName(roleName);
    }

    @Override
    public boolean existsByRoleName(String roleName) {
        return roleRepository.existsByRoleName(roleName);
    }

    @Override
    public boolean save(Role roleToSave) {
        Optional<Role> roleFromDB;

        if (roleToSave.getAuthority() != null) {
            roleFromDB = roleRepository.findByRoleName(roleToSave.getAuthority());
        } else {
            roleFromDB = roleRepository.findById(roleToSave.getId());
        }

        if (roleFromDB.isPresent()) {
            return false;
        } else {
            roleToSave.setRoleName(convertToRoleStyle(roleToSave.getRoleName()));
            roleRepository.save(roleToSave);
            log.info("{} has been created", roleToSave.getRoleName());
            return true;
        }
    }

    @Override
    public boolean update(Role updatedRole) {
        Optional<Role> roleFromDB = roleRepository.findById(updatedRole.getId());
        if (roleFromDB.isEmpty()) {
            return false;
        } else {
            Role roleToUpdate = roleFromDB.get();
            roleToUpdate.setRoleName(convertToRoleStyle(updatedRole.getRoleName()));
            roleToUpdate.setDescription(updatedRole.getDescription());
            roleRepository.save(roleToUpdate);
            log.info("{} updated successfully", roleFromDB.get().getRoleName());
            return true;
        }
    }

    @Override
    public boolean update(Role roleToUpdate, Role updatedRole) {
        Optional<Role> roleFromDB = roleRepository.findByRoleName(roleToUpdate.getRoleName());
        if (roleFromDB.isEmpty()) {
            return false;
        } else {
            Role roleToSave = roleFromDB.get();
            roleToSave.setRoleName(updatedRole.getRoleName());
            roleToSave.setDescription(updatedRole.getDescription());
            roleRepository.save(roleToSave);
            log.info("{} updated successfully", roleFromDB.get().getRoleName());
            return true;
        }
    }

    @Override
    public boolean delete(Role roleToDelete) {

        if (!roleRepository.existsByRoleName(roleToDelete.getRoleName())) {
            return false;
        }

        Optional<Role> roleFromDB = roleRepository.findByRoleName(roleToDelete.getRoleName());

        if (roleFromDB.isEmpty()) {
            return false;
        }

        roleRepository.delete(roleFromDB.get());

        log.info("Role {} removed successfully", roleFromDB.get().getRoleName());

        return true;
    }

    public String convertToRoleStyle(String string) {
        return string.toUpperCase(Locale.ROOT).replaceAll(" ", "_");
    }
}