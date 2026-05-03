package com.example.generation.controllers;

import com.example.generation.dtos.RequestDTOs.TransactionFilterRequest;
import com.example.generation.dtos.ResponseDTOs.TransactionSummaryResponse;
import com.example.generation.services.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Transactions", description = "Operations for managing transactions")
@RestController
@RequestMapping("/api")
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/employee/transactions")
    @Operation(summary = "Get paginated list of all transactions", description = "Retrieve a paginated list of all transactions. Restricted to employees.")
    public Map<String, Object> getPaginatedTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<TransactionSummaryResponse> transactionPage = transactionService.getPaginatedTransactions(pageable);

        return Map.of(
                "content", transactionPage.getContent(),
                "page", transactionPage.getNumber(),
                "size", transactionPage.getSize(),
                "totalElements", transactionPage.getTotalElements(),
                "totalPages", transactionPage.getTotalPages()
        );
    }

    @GetMapping("/customer/transactions")
    @Operation(summary = "Get transaction history for the authenticated customer", description = "Retrieve a paginated and filterable transaction history for the currently logged-in customer.")
    public ResponseEntity<Map<String, Object>> getCustomerTransactions(
            @ModelAttribute TransactionFilterRequest filters,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {

        if (filters.amountEq() != null && (filters.amountLt() != null || filters.amountGt() != null)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Cannot combine amountEq with amountLt or amountGt"));
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<TransactionSummaryResponse> transactionPage = transactionService.getFilteredTransactionsForCustomer(authentication.getName(), filters, pageable);

        return ResponseEntity.ok(Map.of(
                "content", transactionPage.getContent(),
                "page", transactionPage.getNumber(),
                "size", transactionPage.getSize(),
                "totalElements", transactionPage.getTotalElements(),
                "totalPages", transactionPage.getTotalPages()
        ));
    }
}
