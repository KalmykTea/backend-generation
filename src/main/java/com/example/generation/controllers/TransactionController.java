package com.example.generation.controllers;

import com.example.generation.dtos.RequestDTOs.TransactionRequestDTO;
import com.example.generation.dtos.ResponseDTOs.TransactionResponseDTO;
import com.example.generation.entities.Transaction;
import com.example.generation.enums.TransactionType;
import com.example.generation.mappers.ResponseDTOMappers.TransactionResponseDTOMapper;
import com.example.generation.dtos.RequestDTOs.TransactionFilterRequest;
import com.example.generation.dtos.ResponseDTOs.TransactionResponseDTO;
import com.example.generation.services.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@Tag(name = "Transactions", description = "Operations for managing transactions")
@RestController
@RequestMapping("/transactions")
public class TransactionController {
    final private TransactionService transactionService;
    private final TransactionResponseDTOMapper transactionResponseDTOMapper;

    public TransactionController(TransactionService transactionService,  TransactionResponseDTOMapper transactionResponseDTOMapper) {
        this.transactionService = transactionService;
        this.transactionResponseDTOMapper = transactionResponseDTOMapper;
    }

    @PostMapping("/transfer")
    @Operation(summary = "Transfer money", description = "Transfer funds to another account")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Transfer completed successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TransactionResponseDTO.class),
                            examples = @ExampleObject(
                                    name = "Transfer response",
                                    value = """
                                {
                                  "id": 1,
                                  "fromAccount": {
                                    "iban": "NL13INHO0162593609",
                                    "userId": 2,
                                    "accountType": "CHECKING"
                                  },
                                  "toAccount": {
                                    "iban": "NL18INHO0398474392",
                                    "userId": 4,
                                    "accountType": "CHECKING"
                                  },
                                  "initiatedBy": {
                                    "id": 2,
                                    "firstName": "Jan",
                                    "lastName": "Jansen"
                                  },
                                  "amount": 250.00,
                                  "description": "Gift money :)",
                                  "transactionType": "TRANSFER"
                                }
                                """
                            )
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Insufficient balance or daily limit reached", content = @Content),
            @ApiResponse(responseCode = "404", description = "Account not found", content = @Content)
    })
    public TransactionResponseDTO transfer(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TransactionRequestDTO.class),
                            examples = @ExampleObject(
                                    name = "Transfer request",
                                    value = """
                                {
                                  "fromAccount": {
                                    "iban": "NL13INHO0162593609",
                                    "userId": 2,
                                    "accountType": "CHECKING"
                                  },
                                  "toAccount": {
                                    "iban": "NL18INHO0398474392",
                                    "userId": 4,
                                    "accountType": "CHECKING"
                                  },
                                  "initiatedBy": {
                                    "id": 2,
                                    "firstName": "Jan",
                                    "lastName": "Jansen"
                                  },
                                  "amount": 250.00,
                                  "description": "Gift money :)",
                                  "transactionType": "TRANSFER"
                                }
                                """
                            )
                    )
            )
            @RequestBody TransactionRequestDTO transactionRequestDTO
    ) {
        return transactionService.processTransaction(transactionRequestDTO, TransactionType.TRANSFER);
    }

    @PostMapping("/withdraw")
    @Operation(summary = "Withdraw money", description = "Withdraw funds from an account via ATM")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Withdrawal completed successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TransactionResponseDTO.class),
                            examples = @ExampleObject(
                                    name = "Withdraw response",
                                    value = """
                                            {
                                              "fromAccount": {
                                                "iban": "NL13INHO0162593609",
                                                "userId": 2,
                                                "accountType": "CHECKING"
                                              },
                                              "toAccount": {
                                                "iban": "NL13INHO0162593609",
                                                "userId": 2,
                                                "accountType": "CHECKING"
                                              },
                                              "initiatedBy": {
                                                "id": 2,
                                                "firstName": "Jan",
                                                "lastName": "Jansen"
                                              },
                                              "amount": 250.00,
                                              "description": "ATM withdrawal",
                                              "transactionType": "WITHDRAWAL"
                                            }
                                """
                            )
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Insufficient balance or daily limit reached", content = @Content),
            @ApiResponse(responseCode = "404", description = "Account not found", content = @Content)
    })
    public TransactionResponseDTO withdraw(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TransactionRequestDTO.class),
                            examples = @ExampleObject(
                                    name = "Withdraw request",
                                    value = """
                                            {
                                              "fromAccount": {
                                                "iban": "NL13INHO0162593609",
                                                "userId": 2,
                                                "accountType": "CHECKING"
                                              },
                                              "toAccount": {
                                                "iban": "NL13INHO0162593609",
                                                "userId": 2,
                                                "accountType": "CHECKING"
                                              },
                                              "initiatedBy": {
                                                "id": 2,
                                                "firstName": "Jan",
                                                "lastName": "Jansen"
                                              },
                                              "amount": 250.00,
                                              "description": "ATM withdrawal",
                                              "transactionType": "WITHDRAWAL"
                                            }
                                """
                            )
                    )
            )
            @RequestBody TransactionRequestDTO transactionRequestDTO
    ) {
        return transactionService.processTransaction(transactionRequestDTO, TransactionType.WITHDRAWAL);
    }

    @PostMapping("/deposit")
    @Operation(summary = "Deposit money", description = "Deposit funds into an account via ATM")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Deposit completed successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TransactionResponseDTO.class),
                            examples = @ExampleObject(
                                    name = "Deposit response",
                                    value = """
                                            {
                                              "fromAccount": {
                                                "iban": "NL13INHO0162593609",
                                                "userId": 2,
                                                "accountType": "CHECKING"
                                              },
                                              "toAccount": {
                                                "iban": "NL13INHO0162593609",
                                                "userId": 2,
                                                "accountType": "CHECKING"
                                              },
                                              "initiatedBy": {
                                                "id": 2,
                                                "firstName": "Jan",
                                                "lastName": "Jansen"
                                              },
                                              "amount": 250.00,
                                              "description": "ATM deposit",
                                              "transactionType": "DEPOSIT"
                                            }
                                """
                            )
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Insufficient balance or daily limit reached", content = @Content),
            @ApiResponse(responseCode = "404", description = "Account not found", content = @Content)
    })
    public TransactionResponseDTO deposit(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TransactionRequestDTO.class),
                            examples = @ExampleObject(
                                    name = "Deposit request",
                                    value = """
                                            {
                                              "fromAccount": {
                                                "iban": "NL13INHO0162593609",
                                                "userId": 2,
                                                "accountType": "CHECKING"
                                              },
                                              "toAccount": {
                                                "iban": "NL13INHO0162593609",
                                                "userId": 2,
                                                "accountType": "CHECKING"
                                              },
                                              "initiatedBy": {
                                                "id": 2,
                                                "firstName": "Jan",
                                                "lastName": "Jansen"
                                              },
                                              "amount": 250.00,
                                              "description": "ATM deposit",
                                              "transactionType": "DEPOSIT"
                                            }
                                """
                            )
                    )
            )
            @RequestBody TransactionRequestDTO transactionRequestDTO
    ) {
        return transactionService.processTransaction(transactionRequestDTO, TransactionType.DEPOSIT);
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

    @GetMapping("/{userId}")
    @Operation(summary = "Search and filter customer transactions", description = "Retrieve a paginated list of transactions for the authenticated customer with optional filters.")
    public Map<String, Object> getCustomerTransactions(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) BigDecimal amountLt,
            @RequestParam(required = false) BigDecimal amountGt,
            @RequestParam(required = false) BigDecimal amountEq,
            @RequestParam(required = false) String iban,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @PathVariable Long userId) {

        TransactionFilterRequest filters = new TransactionFilterRequest(startDate, endDate, amountLt, amountGt, amountEq, iban);
        Pageable pageable = PageRequest.of(page, size);
        Page<TransactionResponseDTO> transactionPage = transactionService.getFilteredTransactions(filters, pageable, userId);

        return Map.of(
                "content", transactionPage.getContent(),
                "page", transactionPage.getNumber(),
                "size", transactionPage.getSize(),
                "totalElements", transactionPage.getTotalElements(),
                "totalPages", transactionPage.getTotalPages()
        );
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @Operation(summary = "Get paginated list of all transactions", description = "Retrieve a paginated list of all transactions. Restricted to employees.")
    public Map<String, Object> getPaginatedTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<TransactionResponseDTO> transactionPage = transactionService.getPaginatedTransactions(pageable);

        return Map.of(
                "content", transactionPage.getContent(),
                "page", transactionPage.getNumber(),
                "size", transactionPage.getSize(),
                "totalElements", transactionPage.getTotalElements(),
                "totalPages", transactionPage.getTotalPages()
        );
    }
    @Operation(summary = "Get transactions per account")
    @GetMapping("/{iban}/transactions")
    public ResponseEntity<Page<TransactionResponseDTO>> getTransactionsByAccountIBAN(@PathVariable String iban, Pageable pageable) {
        Page<TransactionResponseDTO> result = transactionService.getTransactionsByAccountIBAN(iban, pageable);

        return new ResponseEntity<Page<TransactionResponseDTO>>(result, HttpStatus.OK);
    }
}
