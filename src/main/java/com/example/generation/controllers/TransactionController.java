package com.example.generation.controllers;

import com.example.generation.dtos.RequestDTOs.TransactionFilterRequest;
import com.example.generation.dtos.ResponseDTOs.TransactionResponseDTO;
import com.example.generation.services.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
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

    @GetMapping
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
}
