package com.github.vladyslavbabenko.mycoloroflife.service.implementation;

import com.github.vladyslavbabenko.mycoloroflife.entity.User;
import com.github.vladyslavbabenko.mycoloroflife.service.BruteForceProtectionService;
import com.github.vladyslavbabenko.mycoloroflife.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;

/**
 * Implementation of {@link BruteForceProtectionService}.
 */

@Service
public class BruteForceProtectionServiceImpl implements BruteForceProtectionService {

    private final UserService userService;

    @Value("${security.failed.login.count}")
    private int maxFailedLogins;

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    public BruteForceProtectionServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void registerLoginFailure(String username) {
        User userFromDB = getUser(username);

        if (userFromDB != null && userFromDB.isAccountNonLocked()) {
            int failedCounter = userFromDB.getFailedLoginAttempt();

            if (maxFailedLogins < failedCounter + 1) {
                userFromDB.setAccountNonLocked(false);
                log.info("Login disabled for user with username - {}", userFromDB.getUsername());
            } else {
                userFromDB.setFailedLoginAttempt(failedCounter + 1);
            }

            userService.updateUser(userFromDB);
        }
    }

    @Override
    public void resetBruteForceCounter(String username) {
        User userFromDB = getUser(username);

        if (userFromDB == null || userFromDB.getFailedLoginAttempt() == 0) {
            return;
        }

        userFromDB.setFailedLoginAttempt(0);
        userFromDB.setAccountNonLocked(true);

        userService.updateUser(userFromDB);

        log.info("Failed login attempt set to 0 for user with username - {}", userFromDB.getUsername());
    }

    @Override
    public boolean isBruteForceAttack(String username) {
        User userFromDB = getUser(username);

        if (userFromDB != null) {
            boolean isBruteForceAttack = userFromDB.getFailedLoginAttempt() >= maxFailedLogins;

            if (isBruteForceAttack) {
                log.info("Registered brute force attack on user with username - {} ", userFromDB.getUsername());
            }

            return isBruteForceAttack;
        }

        return false;
    }

    private User getUser(String username) {
        return (User) userService.loadUserByUsername(username);
    }

    public int getMaxFailedLogins() {
        return maxFailedLogins;
    }
}