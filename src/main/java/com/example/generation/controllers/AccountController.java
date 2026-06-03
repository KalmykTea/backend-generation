package com.example.generation.controllers;

import com.example.generation.dtos.RequestDTOs.AccountLimitsRequestDTO;
import com.example.generation.dtos.ResponseDTOs.AccountClosureResponse;
import com.example.generation.dtos.ResponseDTOs.AccountFullResponseDTO;
import com.example.generation.dtos.ResponseDTOs.AccountLimitsResponseDTO;
import com.example.generation.entities.Account;
import com.example.generation.framework.groups.OnUpdate;
import com.example.generation.mappers.ResponseDTOMappers.AccountFullResponseDTOMapper;
import com.example.generation.services.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import java.util.Map;

@Tag(name = "Accounts", description = "Operations for managing accounts")
@RestController
@RequestMapping("accounts")
public class AccountController {
    final private AccountService accountService;
    private final AccountFullResponseDTOMapper accountFullResponseDTOMapper;

    public AccountController(
            AccountService accountService,
            AccountFullResponseDTOMapper accountFullResponseDTOMapper) {
        this.accountService = accountService;
        this.accountFullResponseDTOMapper = accountFullResponseDTOMapper;
    }

    //view all customer accounts

    @GetMapping
    @PreAuthorize("hasAuthority('EMPLOYEE')")
    @Operation(summary = "Get paginated list of all customer accounts", description = "Retrieve a paginated list of all customer accounts. Restricted to employees.")
    public ResponseEntity<Page<AccountFullResponseDTO>> getPaginatedAccounts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<AccountFullResponseDTO> accountPage = accountService.getPaginatedAccounts(pageable);

        return new ResponseEntity<>(accountPage, HttpStatus.OK);
    }

    //close account

    @PatchMapping("/{iban}/close")
    @PreAuthorize("hasAuthority('EMPLOYEE')")
    @Operation(summary = "Close a customer account", description = "Soft deactivates a customer account by setting its status to CLOSED. Restricted to employees.")
    public ResponseEntity<AccountClosureResponse> closeAccount(@PathVariable String iban) {
        return ResponseEntity.ok(accountService.closeAccount(iban));
    }

    // controller methods based on user stories with swagger doc code go here
    @PatchMapping("/{iban}")
    @Operation(summary = "Update an account", description = "Updates an existing account for the given IBAN using the provided payload.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Account updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AccountLimitsResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Account not found",
                    content = @Content
            ),

    })
    @PreAuthorize("hasAuthority('EMPLOYEE')")
    public AccountLimitsResponseDTO update(
            @Parameter(description = "IBAN of the account to update")
            @PathVariable String iban,
            @Parameter(description = "Account payload used to update an existing account")
            @Validated({OnUpdate.class})
            @RequestBody
            AccountLimitsRequestDTO accountLimitsRequestDTO
    ) {
            return accountService.update(accountLimitsRequestDTO, iban);
    }

    @GetMapping("/user")
    @Operation(summary = "Get accounts by user", description = "Returns all accounts belonging to a specific user")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Accounts retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AccountLimitsResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content
            )
    })
    public List<AccountFullResponseDTO> getAccountsByUserId(
            @RequestParam Long userId
    ) {
        List<Account> accounts = accountService.findAccountsByUserId(userId);
        return accounts.stream()
                .map(accountFullResponseDTOMapper::toDTO)
                .toList();
    }

    @Operation(summary = "Get bank account by IBAN")
    @GetMapping("/iban")
    public ResponseEntity<AccountFullResponseDTO> getAccountByIban(@RequestParam String iban) {
        AccountFullResponseDTO result = accountService.getAccountByIban(iban);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(summary = "Find user IBAN by first and last name")
    @GetMapping("/search")
    @PreAuthorize("hasAuthority('CUSTOMER') or hasAuthority('EMPLOYEE')")
    public ResponseEntity<List<String>> getIbanByName(@RequestParam String firstName, @RequestParam String lastName) {
        List<String> ibans = accountService.getIbansByUserName(firstName, lastName);

        return new ResponseEntity<>(ibans, HttpStatus.OK);
    }

}
