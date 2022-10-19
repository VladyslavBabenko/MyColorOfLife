package com.github.vladyslavbabenko.mycoloroflife.event;

import com.github.vladyslavbabenko.mycoloroflife.entity.User;
import com.github.vladyslavbabenko.mycoloroflife.service.BruteForceProtectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationSuccessListener implements ApplicationListener<AuthenticationSuccessEvent> {
    private final BruteForceProtectionService bruteForceProtectionService;

    @Autowired
    public AuthenticationSuccessListener(BruteForceProtectionService bruteForceProtectionService) {
        this.bruteForceProtectionService = bruteForceProtectionService;
    }

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        bruteForceProtectionService.resetBruteForceCounter(((User) event.getAuthentication().getPrincipal()).getUsername());
    }
}