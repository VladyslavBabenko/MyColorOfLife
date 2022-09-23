package com.github.vladyslavbabenko.mycoloroflife.service;

import com.github.vladyslavbabenko.mycoloroflife.entity.*;
import com.github.vladyslavbabenko.mycoloroflife.enumeration.UserRegistrationType;
import com.github.vladyslavbabenko.mycoloroflife.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Implementation of {@link UserService}.
 */

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ActivationCodeService activationCodeService;
    private final RoleService roleService;
    private final CourseProgressService courseProgressService;
    private final CourseService courseService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           BCryptPasswordEncoder bCryptPasswordEncoder,
                           ActivationCodeService activationCodeService,
                           RoleService roleService,
                           CourseProgressService courseProgressService,
                           CourseService courseService) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.activationCodeService = activationCodeService;
        this.roleService = roleService;
        this.courseProgressService = courseProgressService;
        this.courseService = courseService;
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        if (principal instanceof User) {
            Optional<User> userFromDB = userRepository.findById(((User) authentication.getPrincipal()).getId());
            if (userFromDB.isPresent()) {
                return userFromDB.get();
            }
        } else if (principal instanceof OAuth2User) {
            return (User) loadUserByUsername(((OAuth2User) principal).getAttribute("email"));
        }

        return new User();
    }

    public boolean saveOAuth2User(OAuth2UserAuthority oAuth2UserAuthority) {
        return saveUser(User.builder()
                .username((String) oAuth2UserAuthority.getAttributes().get("name"))
                .email((String) oAuth2UserAuthority.getAttributes().get("email"))
                .registrationType(UserRegistrationType.GMAIL_AUTHENTICATION)
                .build());
    }

    @Override
    public User findById(Integer userId) {
        Optional<User> userFromDB = userRepository.findById(userId);
        return userFromDB.orElseGet(User::new);
    }

    @Override
    public List<User> getAllUsers() {
        List<User> users = userRepository.findAll();
        users.sort(Comparator.comparingInt(User::getId));
        return users;
    }

    @Override
    public String encodePassword(CharSequence rawPassword) {
        if (rawPassword != null && (rawPassword.length() >= 5 && rawPassword.length() <= 30)) {
            return bCryptPasswordEncoder.encode(rawPassword);
        } else return null;
    }

    @Override
    public boolean saveUser(User userToSave) {
        Optional<User> userFromDB;

        if (userToSave.getId() != null) {
            userFromDB = userRepository.findById(userToSave.getId());
        } else {
            userFromDB = userRepository.findByEmail(userToSave.getEmail());
        }

        if (userFromDB.isPresent()) {
            return false;
        } else {
            userToSave.setRoles(Collections.singleton(Role.builder().id(1).roleName("ROLE_USER").build()));
            userToSave.setPassword(encodePassword(userToSave.getPassword()));
            userRepository.save(userToSave);
            return true;
        }
    }

    @Override
    public boolean deleteUser(Integer userId) {
        if (userRepository.existsById(userId)) {
            userRepository.deleteById(userId);
            return true;
        } else return false;
    }

    @Override
    public boolean updateUser(User updatedUser) {
        Optional<User> userFromDB = userRepository.findById(updatedUser.getId());
        if (userFromDB.isEmpty()) {
            return false;
        } else {
            User userToUpdate = userFromDB.get();
            userToUpdate.setEmail(updatedUser.getEmail());
            userRepository.save(userToUpdate);
            return true;
        }
    }

    @Override
    public boolean matchesPassword(User user, CharSequence rawPassword) {
        return bCryptPasswordEncoder.matches(rawPassword, user.getPassword());
    }

    @Override
    public boolean changePassword(User updatedUser) {
        Optional<User> userFromDB = userRepository.findById(updatedUser.getId());
        if (userFromDB.isPresent()) {
            User userToUpdate = userFromDB.get();
            userToUpdate.setPassword(encodePassword(updatedUser.getPassword()));
            userRepository.save(userToUpdate);
            return true;
        } else return false;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userFromDB = userRepository.findByEmail(username);
        if (userFromDB.isEmpty()) {
            throw new UsernameNotFoundException("User with " + username + " not found");
        }
        return userFromDB.get();
    }

    @Override
    public boolean existsById(Integer userId) {
        return userRepository.existsById(userId);
    }

    @Override
    public boolean activateCode(ActivationCode activationCode) {
        if (activationCodeService.existsByCode(activationCode.getCode())) {
            String courseOwnerRoleString = "ROLE_COURSE_OWNER_" +
                    roleService.convertToRoleStyle(activationCode.getCourseTitle().getTitle());
            if (roleService.existsByRoleName(courseOwnerRoleString)) {
                Optional<Role> courseOwnerRole = roleService.findByRoleName(courseOwnerRoleString);
                if (courseOwnerRole.isPresent()) {
                    User user = activationCode.getUser();
                    user.getRoles().add(courseOwnerRole.get());
                    userRepository.save(user);

                    Optional<Course> courseFromDB = courseService.findByCourseTitleAndPage(activationCode.getCourseTitle().getTitle(), 1);
                    courseFromDB.ifPresent(course -> courseProgressService.save(CourseProgress.builder().user(user).course(course).build()));

                    activationCodeService.deleteByCode(activationCode.getCode());
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean deleteRoleFromUser(List<User> users, Role roleToDelete) {
        if (roleService.existsByRoleName(roleToDelete.getRoleName())) {
            getAllUsers().stream().peek(user -> user.getRoles().removeIf(role -> role.getRoleName().equals(roleToDelete.getRoleName()))).forEach(userRepository::save);
            return true;
        }
        return false;
    }
}