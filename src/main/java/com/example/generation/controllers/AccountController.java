package com.example.generation.controllers;

import com.example.generation.services.AccountService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Accounts", description = "Operations for managing accounts")
@RestController
@RequestMapping("accounts")
public class AccountController {
    final private AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    // controller methods based on user stories with swagger doc code go here
}
