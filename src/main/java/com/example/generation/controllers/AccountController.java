package com.example.generation.controllers;

import com.example.generation.dtos.RequestDTOs.AccountRequestDTO;
import com.example.generation.dtos.RequestDTOs.TransactionRequestDTO;
import com.example.generation.entities.Account;
import com.example.generation.framework.groups.OnTransaction;
import com.example.generation.framework.groups.OnUpdate;
import com.example.generation.mappers.RequestDTOMappers.AccountRequestDTOMapper;
import com.example.generation.mappers.RequestDTOMappers.TransactionRequestDTOMapper;
import com.example.generation.services.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.groups.Default;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Accounts", description = "Operations for managing accounts")
@RestController
@RequestMapping("accounts")
public class AccountController {
    final private AccountService accountService;
    final private AccountRequestDTOMapper accountRequestDTOMapper;
    private final TransactionRequestDTOMapper transactionRequestDTOMapper;

    public AccountController(
            AccountService accountService,
            AccountRequestDTOMapper accountRequestDTOMapper,
            TransactionRequestDTOMapper transactionRequestDTOMapper
    ) {
        this.accountService = accountService;
        this.accountRequestDTOMapper = accountRequestDTOMapper;
        this.transactionRequestDTOMapper = transactionRequestDTOMapper;
    }

    // controller methods based on user stories with swagger doc code go here
    @PutMapping("/{id}")
    @Operation(summary = "Update an account", description = "Updates an existing account for the given id using the provided payload.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Account updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Account.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Account not found",
                    content = @Content
            )
    })
    public Account update(
            @Parameter(description = "Account payload used to update an existing account")
            @Validated({OnUpdate.class})
            @RequestBody
            AccountRequestDTO accountRequestDTO,
            @PathVariable Long id
    ) {
            return accountService.update(accountRequestDTOMapper.toEntity(accountRequestDTO), id);
    }

    @PutMapping("/{id}/transactions")
    @Operation(summary = "Withdraw or Deposit", description = "Updates an existing account balance and records the transaction for the given id using the provided payload.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Balance updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Account.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Account not found",
                    content = @Content
            )
    })
    public Account withdrawOrDeposit(
            @Parameter(description = "Transaction payload used to update an existing account balance")
            @Validated({OnTransaction.class, Default.class})
            @RequestBody TransactionRequestDTO transactionRequestDTO,
            @PathVariable Long id
            )
    {
        return accountService.withdrawOrDeposit(id, transactionRequestDTOMapper.toEntity(transactionRequestDTO));
    }


}
