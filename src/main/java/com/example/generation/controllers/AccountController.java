package com.example.generation.controllers;

import com.example.generation.dtos.ResponseDTOs.AccountResponseDTO;
import com.example.generation.dtos.ResponseDTOs.TransactionResponseDTO;
import com.example.generation.services.AccountService;
import com.example.generation.services.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Accounts", description = "Operations for managing accounts")
@RestController
@RequestMapping("accounts")
public class AccountController {
    final private AccountService accountService;
    final private TransactionService transactionService;

    public AccountController(AccountService accountService, TransactionService transactionService) {
        this.accountService = accountService;
        this.transactionService = transactionService;
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

    @Operation(summary = "Get transactions per account")
    @GetMapping("/{id}/transactions")
    public ResponseEntity<Page<TransactionResponseDTO>> getTransactionsByAccountId(@PathVariable Long id, Pageable pageable) {
        Page<TransactionResponseDTO> result = transactionService.getTransactionsByAccountId(id, pageable);

        return new ResponseEntity<Page<TransactionResponseDTO>>(result, HttpStatus.OK);
    }
}
