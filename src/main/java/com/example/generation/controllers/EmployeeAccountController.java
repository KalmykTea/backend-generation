package com.example.generation.controllers;

import com.example.generation.dtos.ResponseDTOs.AccountClosureResponse;
import com.example.generation.dtos.ResponseDTOs.EmployeeAccountResponseDTO;
import com.example.generation.services.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Employee Accounts", description = "Operations for employees to manage customer accounts")
@RestController
@RequestMapping("/api/employee/accounts")
public class EmployeeAccountController {

    private final AccountService accountService;

    public EmployeeAccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    @PreAuthorize("hasRole('EMPLOYEE')")
    @Operation(summary = "Get paginated list of all customer accounts", description = "Retrieve a paginated list of all customer accounts. Restricted to employees.")
    public Map<String, Object> getPaginatedAccounts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<EmployeeAccountResponseDTO> accountPage = accountService.getPaginatedAccounts(pageable);

        return Map.of(
                "content", accountPage.getContent(),
                "page", accountPage.getNumber(),
                "size", accountPage.getSize(),
                "totalElements", accountPage.getTotalElements(),
                "totalPages", accountPage.getTotalPages()
        );
    }
    @PatchMapping("/{accountId}/close")
    @PreAuthorize("hasRole('EMPLOYEE')")
    @Operation(summary = "Close a customer account", description = "Soft deactivates a customer account by setting its status to CLOSED. Restricted to employees.")
    public ResponseEntity<AccountClosureResponse> closeAccount(@PathVariable Long accountId) {
        return ResponseEntity.ok(accountService.closeAccount(accountId));
    }
}
