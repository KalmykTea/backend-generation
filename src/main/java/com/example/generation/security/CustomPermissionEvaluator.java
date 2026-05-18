package com.example.generation.security;

import com.example.generation.dtos.RequestDTOs.TransactionRequestDTO;
import com.example.generation.entities.User;
import com.example.generation.repositories.AccountRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.io.Serializable;
import java.util.Objects;

@Component("permissionEvaluator")
public class CustomPermissionEvaluator implements PermissionEvaluator {

    private final AccountRepository accountRepository;
    public CustomPermissionEvaluator(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        return false; // custom code here
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        return false; // custom code here
    }

    // you can't withdraw or deposit
    // if you don't own the account that you send in the request
    public boolean canUseATM(Authentication authentication, Object targetDomainObject) {
        User user = getAuthenticatedUser(authentication);
        if (targetDomainObject instanceof TransactionRequestDTO dto && isCustomer(authentication)) {
            switch (dto.getTransactionType()) {
                case DEPOSIT:
                    return accountRepository.existsByIbanAndUserId(dto.getToAccount().getIban(), user.getId());
                case WITHDRAWAL:
                    return accountRepository.existsByIbanAndUserId(dto.getFromAccount().getIban(), user.getId());
            }
        }
        return false;
    }

    private User getAuthenticatedUser(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof User user)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }
        return user;
    }

    private boolean isCustomer(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(a -> Objects.equals(a.getAuthority(), "CUSTOMER"));
    }
}
