package com.github.vladyslavbabenko.mycoloroflife.event;

import com.github.vladyslavbabenko.mycoloroflife.service.BruteForceProtectionService;
import com.github.vladyslavbabenko.mycoloroflife.service.PasswordRecoveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationFailureListener implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {
    private final BruteForceProtectionService bruteForceProtectionService;
    private final PasswordRecoveryService passwordRecoveryService;

    @Autowired
    public AuthenticationFailureListener(BruteForceProtectionService bruteForceProtectionService,
                                         PasswordRecoveryService passwordRecoveryService) {
        this.bruteForceProtectionService = bruteForceProtectionService;
        this.passwordRecoveryService = passwordRecoveryService;
    }

    @Override
    public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent event) {
        String username = event.getAuthentication().getName();

        bruteForceProtectionService.registerLoginFailure(username);
        if (bruteForceProtectionService.isBruteForceAttack(username)) {
            passwordRecoveryService.loginDisabled(username);
        }
    }
}