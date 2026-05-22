package com.example.generation.security;

import com.example.generation.domain.policy.CPEPolicy;
import com.example.generation.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CPEPolicyTest {
    Authentication authentication;
    Authentication nullAuthentication;
    CPEPolicy policy;

    @BeforeEach
    void setUp()
    {
        policy = new CPEPolicy();
        authentication = new UsernamePasswordAuthenticationToken(new User(), "password");
        nullAuthentication = null;
    }

    @Test
    void enforceUserIsAuthenticated_throwsForUnauthorizedIsUser()
    {
        assertThrows(ResponseStatusException.class, ()->
                policy.enforceUserIsAuthenticated(nullAuthentication));
    }

    @Test
    void enforceUserIsAuthenticated_allowsAuthorisedUser(){
        assertDoesNotThrow(()->
                policy.enforceUserIsAuthenticated(authentication));
    }
}
