package com.github.vladyslavbabenko.mycoloroflife.service.implementation;

import com.github.vladyslavbabenko.mycoloroflife.entity.*;
import com.github.vladyslavbabenko.mycoloroflife.enumeration.UserRegistrationType;
import com.github.vladyslavbabenko.mycoloroflife.repository.UserRepository;
import com.github.vladyslavbabenko.mycoloroflife.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.*;

/**
 * Implementation of {@link UserService}.
 */

@Service
public class UserServiceImpl implements UserService {

    private final RoleService roleService;
    private final CourseService courseService;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ActivationCodeService activationCodeService;
    private final CourseProgressService courseProgressService;

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

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
            Optional<User> userFromDB = userRepository.findByEmail(((User) authentication.getPrincipal()).getEmail().toLowerCase(Locale.ROOT));

            if (userFromDB.isPresent()) {
                return userFromDB.get();
            }

            userFromDB = userRepository.findById(((User) authentication.getPrincipal()).getId());

            if (userFromDB.isPresent()) {
                return userFromDB.get();
            }

        } else if (principal instanceof OAuth2User) {
            return (User) loadUserByUsername(Objects.requireNonNull(((OAuth2User) principal).getAttribute("email")));
        }

        return new User();
    }

    public boolean saveOAuth2User(OAuth2UserAuthority oAuth2UserAuthority) {
        return saveUser(User.builder()
                .name((String) oAuth2UserAuthority.getAttributes().get("name"))
                .email(((String) oAuth2UserAuthority.getAttributes().get("email")))
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
        Optional<User> userFromDB = userRepository.findByEmail(userToSave.getUsername().toLowerCase(Locale.ROOT));

        if (userFromDB.isPresent()) {
            log.warn("User with username {} exists already", userToSave.getUsername());
            return false;
        } else {
            userToSave.setRoles(Collections.singleton(Role.builder().id(1).roleName("ROLE_USER").build()));
            userToSave.setPassword(encodePassword(userToSave.getPassword()));
            userToSave.setEmail(userToSave.getEmail().toLowerCase(Locale.ROOT));

            userRepository.save(userToSave);

            log.info("User with username {} has been created", userToSave.getUsername());

            return true;
        }
    }

    @Override
    public boolean deleteUser(Integer userId) {
        if (userRepository.existsById(userId)) {
            userRepository.deleteById(userId);

            log.info("User with id {} has been deleted", userId);

            return true;
        }

        return false;
    }

    @Override
    public boolean updateUser(User updatedUser) {
        Optional<User> userFromDB = userRepository.findById(updatedUser.getId());
        if (userFromDB.isEmpty()) {
            log.warn("User with {} not found", updatedUser.getUsername());
            return false;
        } else {
            User userToUpdate = userFromDB.get();

            if (!userToUpdate.getEmail().equals(updatedUser.getEmail())) {
                userToUpdate.setEmailConfirmed(false);
            }

            userToUpdate.setEmail(updatedUser.getEmail().toLowerCase(Locale.ROOT));
            userToUpdate.setFailedLoginAttempt(updatedUser.getFailedLoginAttempt());
            userToUpdate.setAccountNonLocked(updatedUser.isAccountNonLocked());

            userRepository.save(userToUpdate);

            log.info("User with username {} has been updated", userFromDB.get().getUsername());

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

            log.info("Password for user with username {} has been updated", userFromDB.get().getUsername());

            return true;
        }

        log.warn("User with id {} not found", updatedUser.getId());

        return false;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userFromDB = userRepository.findByEmail(username.toLowerCase(Locale.ROOT));
        if (userFromDB.isEmpty()) {
            log.warn("User with {} not found", username);
            throw new UsernameNotFoundException("User with " + username + " not found");
        }

        boolean isAdmin = userFromDB.get().getRoles().stream().anyMatch(role -> role.getRoleName().equals("ROLE_ADMIN"));

        if (isAdmin) {
            log.info("Found user with username {} and ROLE_ADMIN authority", userFromDB.get().getUsername());
        }

        return userFromDB.get();
    }

    @Override
    public boolean existsById(Integer userId) {
        return userRepository.existsById(userId);
    }

    @Override
    public boolean activateCode(ActivationCode activationCode) {
        if (!activationCodeService.existsByCode(activationCode.getCode())) {
            return false;
        }

        String courseOwnerRoleString = "ROLE_COURSE_OWNER_" +
                roleService.convertToRoleStyle(activationCode.getCourseTitle().getTitle());

        if (!roleService.existsByRoleName(courseOwnerRoleString)) {
            return false;
        }

        Optional<Role> courseOwnerRole = roleService.findByRoleName(courseOwnerRoleString);

        if (courseOwnerRole.isEmpty()) {
            return false;
        }

        User user = activationCode.getUser();
        user.getRoles().add(courseOwnerRole.get());
        userRepository.save(user);

        Optional<Course> courseFromDB = courseService.findByCourseTitleAndPage(activationCode.getCourseTitle().getTitle(), 1);
        courseFromDB.ifPresent(course -> courseProgressService.save(CourseProgress.builder().user(user).course(course).build()));

        activationCodeService.deleteByCode(activationCode.getCode());

        log.info("ActivationCode {} for User with username {} has been activated", activationCode.getCode(), user.getUsername());

        return true;
    }

    @Override
    public boolean deleteRoleFromUser(List<User> users, Role roleToDelete) {
        if (roleService.existsByRoleName(roleToDelete.getRoleName())) {
            getAllUsers().stream()
                    .peek(user -> user.getRoles().removeIf(role -> role.getRoleName().equals(roleToDelete.getRoleName())))
                    .forEach(userRepository::save);

            log.info("Authority {} has been removed from users", roleToDelete);

            return true;
        }
        return false;
    }

    @Override
    public boolean isAccountNonLocked(String username) {
        return loadUserByUsername(username).isAccountNonLocked();
    }

    @Override
    public boolean confirmEmail(String username) {
        try {
            User userFromDB = (User) loadUserByUsername(username);
            userFromDB.setEmailConfirmed(true);

            userRepository.save(userFromDB);

            log.info("Email has been verified for user with username {}", userFromDB.getUsername());

            return true;

        } catch (UsernameNotFoundException e) {
            log.warn("UsernameNotFoundException in {} : {}", this.getClass().getSimpleName(), e.getMessage());
            return false;
        }
    }
}