package com.example.generation.controllers;

import com.example.generation.dtos.ResponseDTOs.AccountResponseDTO;
import com.example.generation.entities.Account;
import com.example.generation.entities.User;
import com.example.generation.mappers.ResponseDTOMappers.AccountResponseDTOMapper;
import com.example.generation.services.AccountService;
import com.example.generation.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@Tag(name = "Accounts", description = "Operations for managing accounts")
@RestController
@RequestMapping("accounts")
public class AccountController {
    final private AccountService accountService;
    final private AccountResponseDTOMapper accountResponseDTOMapper;
    final private UserService userService;

    public AccountController(AccountService accountService, AccountResponseDTOMapper accountResponseDTOMapper, UserService userService) {
        this.accountService = accountService;
        this.accountResponseDTOMapper = accountResponseDTOMapper;
        this.userService = userService;
    }

    // controller methods based on user stories with swagger doc code go here

    @Operation(summary = "Get user account by IBAN")
    @GetMapping
    public ResponseEntity<AccountResponseDTO> getAccountByIban(@RequestParam String iban) {
        Optional<Account> account = accountService.findByIban(iban);
        // Check if there is an account
        if (account.isPresent()){
            AccountResponseDTO result = accountResponseDTOMapper.toDTO(account.get());
            return new ResponseEntity<>(result, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Find user IBAN by first and last name")
    @GetMapping("/search")
    public ResponseEntity<List<String>> getIbanByName(@RequestParam String firstName, @RequestParam String lastName) {
        List<User> users = userService.findUserByFirstNameAndLastName(firstName, lastName);
        List<String> ibans = users.stream()
                .flatMap(user -> accountService.findByUser(user).stream())
                .map(Account::getIban)
                .toList();
        if (ibans.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(ibans, HttpStatus.OK);
        }
    }
}
