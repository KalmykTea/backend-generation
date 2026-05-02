package com.example.generation.controllers;

import com.example.generation.dtos.ResponseDTOs.AccountResponseDTO;
import com.example.generation.services.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Accounts", description = "Operations for managing accounts")
@RestController
@RequestMapping("accounts")
public class AccountController {
    final private AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    // controller methods based on user stories with swagger doc code go here

    @Operation(summary = "Get user account by IBAN")
    @GetMapping
    public ResponseEntity<AccountResponseDTO> getAccountByIban(@RequestParam String iban) {
        AccountResponseDTO result = accountService.getAccountByIban(iban);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(summary = "Find user IBAN by first and last name")
    @GetMapping("/search")
    public ResponseEntity<List<String>> getIbanByName(@RequestParam String firstName, @RequestParam String lastName) {
        List<String> ibans = accountService.getIbansByUserName(firstName, lastName);

        return new ResponseEntity<>(ibans, HttpStatus.OK);
    }
}
