package com.example.generation.controllers;

import com.example.generation.dtos.RequestDTOs.ATMRequestDTO;
import com.example.generation.dtos.RequestDTOs.TransactionRequestDTO;
import com.example.generation.dtos.ResponseDTOs.ATMResponseDTO;
import com.example.generation.dtos.ResponseDTOs.TransactionResponseDTO;
import com.example.generation.entities.Transaction;
import com.example.generation.mappers.ResponseDTOMappers.TransactionResponseDTOMapper;
import com.example.generation.services.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Transactions", description = "Operations for managing transactions")
@RestController
@RequestMapping("transactions")
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
                                                 "amount": 250.00,
                                                 "description": "Gift money :)",
                                                 "fromAccountIban": "NL67INHO0398474392",
                                                 "id": 10,
                                                 "initiatedBy": {
                                                     "firstName": "Jane",
                                                     "id": 2,
                                                     "lastName": "Doe"
                                                 },
                                                 "toAccountIban": "NL69INHO0398474392",
                                                 "transactionType": "TRANSFER"
                                             }
                                """
                            )
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Insufficient balance or daily limit reached", content = @Content),
            @ApiResponse(responseCode = "404", description = "Account not found", content = @Content)
    })
    @PreAuthorize("hasAuthority('EMPLOYEE') or hasAuthority('CUSTOMER')")
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
                                                "id" : null,
                                                "fromAccountIban": "NL67INHO0398474392",
                                                "toAccountIban": "NL69INHO0398474392",
                                                "amount": 250.00,
                                                "description": "Gift money :)",
                                                "transactionType": "TRANSFER"
                                            }
                                """
                            )
                    )
            )
            @RequestBody @Valid TransactionRequestDTO transactionRequestDTO
    ) {
        return transactionService.processTransfer(transactionRequestDTO);
    }

    @PostMapping("/withdraw")
    @Operation(summary = "Withdraw money", description = "Withdraw funds from an account via ATM")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Withdrawal completed successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ATMResponseDTO.class),
                            examples = @ExampleObject(
                                    name = "Withdraw response",
                                    value = """
                                            {
                                              "iban": "NL13INHO0162593609",
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
    @PreAuthorize("@permissionEvaluator.canUseATM(authentication, #requestDTO)")
    public ATMResponseDTO withdraw(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ATMRequestDTO.class),
                            examples = @ExampleObject(
                                    name = "Withdraw request",
                                    value = """
                                            {
                                              "iban": "NL13INHO0162593609",
                                              "amount": 250.00,
                                              "description": "ATM withdrawal",
                                              "transactionType": "WITHDRAWAL"
                                            }
                                """
                            )
                    )
            )
            @RequestBody @Valid ATMRequestDTO requestDTO
    ) {
        return transactionService.processATMRequest(requestDTO);
    }

    @PostMapping("/deposit")
    @Operation(summary = "Deposit money", description = "Deposit funds into an account via ATM")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Deposit completed successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ATMResponseDTO.class),
                            examples = @ExampleObject(
                                    name = "Deposit response",
                                    value = """
                                            {
                                              "iban": "NL13INHO0162593609",
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
    @PreAuthorize("@permissionEvaluator.canUseATM(authentication, #requestDTO)")
    public ATMResponseDTO deposit(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ATMRequestDTO.class),
                            examples = @ExampleObject(
                                    name = "Deposit request",
                                    value = """
                                            {
                                              "iban": "NL13INHO0162593609",
                                              "amount": 250.00,
                                              "description": "ATM deposit",
                                              "transactionType": "DEPOSIT"
                                            }
                                """
                            )
                    )
            )
            @RequestBody @Valid ATMRequestDTO requestDTO
    ) {
        return transactionService.processATMRequest(requestDTO);
    }

    @GetMapping("/user")
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
    @PreAuthorize("hasAuthority('EMPLOYEE')")
    public Page<TransactionResponseDTO> getTransactionsByUserId(
            @RequestParam Long userId, @PageableDefault(size = 10) Pageable pageable
    ) {
        Page<Transaction> transactions = transactionService.findTransactionsByUserId(userId, pageable);
        return transactions.map(transactionResponseDTOMapper::toDTO);
    }

    @Operation(summary = "Get transactions per account")
    @GetMapping("/{iban}/transactions")
    public ResponseEntity<Page<TransactionResponseDTO>> getTransactionsByAccountIBAN(@PathVariable String iban, Pageable pageable) {
        Page<TransactionResponseDTO> result = transactionService.getTransactionsByAccountIBAN(iban, pageable);

        return new ResponseEntity<Page<TransactionResponseDTO>>(result, HttpStatus.OK);
    }
}
