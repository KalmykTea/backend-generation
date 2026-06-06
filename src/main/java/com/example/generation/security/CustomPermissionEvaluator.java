package com.example.generation.security;

import com.example.generation.domain.policy.CPEPolicy;
import com.example.generation.dtos.RequestDTOs.ATMRequestDTO;
import com.example.generation.entities.User;
import com.example.generation.repositories.AccountRepository;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Objects;

@Component("permissionEvaluator")
public class CustomPermissionEvaluator implements PermissionEvaluator {

    private final AccountRepository accountRepository;
    private final CPEPolicy cpePolicy;
    public CustomPermissionEvaluator(AccountRepository accountRepository,
                                     CPEPolicy cpePolicy) {
        this.accountRepository = accountRepository;
        this.cpePolicy = cpePolicy;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        return false;
    }

    // you can't withdraw or deposit
    // if you don't own the account that you send in the request
    public boolean canUseATM(Authentication authentication, Object targetDomainObject) {
        User user = getAuthenticatedUser(authentication);
        if (targetDomainObject instanceof ATMRequestDTO atmDTO && isCustomer(authentication)) {
            return accountRepository.existsByIbanAndUserId(atmDTO.getIban(), user.getId());
        }
        return false;
    }

    public boolean canViewUserTransactions(Authentication authentication, Object targetDomainObject) {
        User user = getAuthenticatedUser(authentication);
        if (targetDomainObject instanceof Long userId) {
            return user.getId().equals(userId);
        }
        return false;
    }

    private User getAuthenticatedUser(Authentication authentication) {
        cpePolicy.enforceUserIsAuthenticated(authentication);
        return (User) authentication.getPrincipal();
    }

    private boolean isCustomer(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(a -> Objects.equals(a.getAuthority(), "CUSTOMER"));
    }

    public boolean isOwner(Authentication authentication, Long userId) {
        User currentUser = getAuthenticatedUser(authentication);
        if (isCustomer(authentication)) {
            return currentUser.getId().equals(userId);
        }
        return true; //because employees can access anything
    }
}
