package com.github.vladyslavbabenko.mycoloroflife.service.implementation;

import com.github.vladyslavbabenko.mycoloroflife.entity.User;
import com.github.vladyslavbabenko.mycoloroflife.service.BruteForceProtectionService;
import com.github.vladyslavbabenko.mycoloroflife.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link BruteForceProtectionService}.
 */

@Service
public class BruteForceProtectionServiceImpl implements BruteForceProtectionService {
    @Value("${security.failed.login.count}")
    private int maxFailedLogins;

    private final UserService userService;

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
            } else {
                userFromDB.setFailedLoginAttempt(failedCounter + 1);
            }

            userService.updateUser(userFromDB);
        }
    }

    @Override
    public void resetBruteForceCounter(String username) {
        User userFromDB = getUser(username);
        if (userFromDB != null) {
            userFromDB.setFailedLoginAttempt(0);
            userFromDB.setAccountNonLocked(true);

            userService.updateUser(userFromDB);
        }
    }

    @Override
    public boolean isBruteForceAttack(String username) {
        User userFromDB = getUser(username);

        if (userFromDB != null) {
            return userFromDB.getFailedLoginAttempt() >= maxFailedLogins;
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