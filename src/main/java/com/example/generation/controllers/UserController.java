package com.example.generation.controllers;

import com.example.generation.dtos.RequestDTOs.AccountLimitsRequestDTO;
import com.example.generation.dtos.ResponseDTOs.UserResponseDTO;
import com.example.generation.framework.groups.OnCreate;
import com.example.generation.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Users", description = "Operations for managing users")
@RestController
@RequestMapping("users")
public class UserController {
    final private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // controller methods based on user stories with swagger doc code go here

    @Operation(summary = "Get list of customers pending approval")
    @GetMapping("/pending")
    @PreAuthorize("hasAuthority('EMPLOYEE')")
    public ResponseEntity<List<UserResponseDTO>> getPendingUsers() {
        List<UserResponseDTO> result = userService.getPendingUsers();

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(
            summary = "Approve user",
            description = "Approves a user with Pending status. Creates a Current and Savings account with a unique IBAN"
    )
    @PostMapping("/{id}")
    @PreAuthorize("hasAuthority('EMPLOYEE')")
    public ResponseEntity<UserResponseDTO> approveUser(@PathVariable Long id,
                                                       @RequestBody @Validated(OnCreate.class)
                                                       List<AccountLimitsRequestDTO> accountLimitsRequestDTOS) {
        UserResponseDTO result = userService.approveUser(id, accountLimitsRequestDTOS);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
