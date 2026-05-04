package com.example.generation.controllers;

import com.example.generation.dtos.RequestDTOs.TransactionRequestDTO;
import com.example.generation.dtos.ResponseDTOs.AccountResponseDTO;
import com.example.generation.dtos.ResponseDTOs.TransactionResponseDTO;
import com.example.generation.entities.Account;
import com.example.generation.entities.Transaction;
import com.example.generation.mappers.ResponseDTOMappers.TransactionResponseDTOMapper;
import com.example.generation.services.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Transactions", description = "Operations for managing transactions")
@RestController
@RequestMapping("transactions")
public class TransactionController {
    final private TransactionService transactionService;
    final private TransactionResponseDTOMapper  transactionResponseDTOMapper;

    public TransactionController(TransactionService transactionService, TransactionResponseDTOMapper transactionResponseDTOMapper) {
        this.transactionService = transactionService;
        this.transactionResponseDTOMapper = transactionResponseDTOMapper;
    }

    // controller methods based on user stories with swagger doc code go here
    @PostMapping("")
    @Operation(summary = "Transfer funds", description = "Transfers funds between two accounts")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Transfer completed successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TransactionResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Insufficient balance or daily limit reached",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Account not found",
                    content = @Content
            )
    })
    public TransactionResponseDTO transfer(
            @RequestBody TransactionRequestDTO transactionRequestDTO
    ) {
        Transaction transaction = transactionService.transferFunds(
                transactionRequestDTO.getFromAccount().getId(),
                transactionRequestDTO.getToAccount().getId(),
                transactionRequestDTO.getAmount(),
                transactionRequestDTO.getInitiatedBy().getId()
        );
        return transactionResponseDTOMapper.toDTO(transaction);
    }

    @GetMapping("")
    @Operation(summary = "Get transactions by user", description = "Returns all transactions paginated belonging to a specific user")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Transactions retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TransactionResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content
            )
    })
    public Page<TransactionResponseDTO> getTransactionsByUserId(
            @RequestParam Long userId, @PageableDefault(size = 10) Pageable pageable
    ) {
        Page<Transaction> transactions = transactionService.findTransactionsByUserId(userId, pageable);
        return transactions.map(transactionResponseDTOMapper::toDTO);
    }
}
