package com.example.generation.enums;

import jakarta.annotation.Nullable;
import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    EMPLOYEE, CUSTOMER;

    @Override
    public @Nullable String getAuthority() {
        return name();
    }
}
