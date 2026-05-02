package com.example.generation.controllers;

import com.example.generation.dtos.RequestDTOs.TransactionRequestDTO;
import com.example.generation.dtos.ResponseDTOs.TransactionResponseDTO;
import com.example.generation.services.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Transactions", description = "Operations for managing transactions")
@RestController
@RequestMapping("transactions")
public class TransactionController {
    final private TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    // controller methods based on user stories with swagger doc code go here

    @Operation(
            summary = "Transfer funds between accounts",
            description = "Transfers between one customer's account to another's. Transfer limits enforced."
    )
    @PostMapping
    public ResponseEntity<TransactionResponseDTO> createTransaction(@RequestBody TransactionRequestDTO dto) {
        TransactionResponseDTO result = transactionService.createTransaction(dto);

        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

}
