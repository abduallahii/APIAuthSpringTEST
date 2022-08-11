package com.ateamgroup.stoolbe.listener;

import com.ateamgroup.stoolbe.service.LoginAttemptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;


@Component
public class AuthenticationFaliureListener {

    private LoginAttemptService loginAttemptService;
    @Autowired
    public AuthenticationFaliureListener(LoginAttemptService loginAttemptService) {
        this.loginAttemptService = loginAttemptService;
    }

    @EventListener
    public void onAuthenticaionFaliure(AuthenticationFailureBadCredentialsEvent event) throws ExecutionException {
        Object obj = event.getAuthentication().getPrincipal();
        if (obj instanceof String)  {
            String username = (String) event.getAuthentication().getPrincipal();
            loginAttemptService.addUserToLoginAttamptCache(username);
        }
    }

}
