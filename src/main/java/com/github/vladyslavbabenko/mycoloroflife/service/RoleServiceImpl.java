package com.github.vladyslavbabenko.mycoloroflife.service;

import com.github.vladyslavbabenko.mycoloroflife.entity.Role;
import com.github.vladyslavbabenko.mycoloroflife.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
            return true;
        }
    }

    @Override
    public boolean delete(Role roleToDelete) {
        if (roleRepository.existsByRoleName(roleToDelete.getRoleName())) {
            Optional<Role> roleFromDB = roleRepository.findByRoleName(roleToDelete.getRoleName());
            if (roleFromDB.isPresent()) {
                roleRepository.delete(roleFromDB.get());
                return true;
            }
        } else return false;
        return false;
    }

    public String convertToRoleStyle(String string) {
        return string.toUpperCase(Locale.ROOT).replaceAll(" ", "_");
    }
}