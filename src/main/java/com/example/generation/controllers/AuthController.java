package com.example.generation.controllers;

import com.example.generation.dtos.RequestDTOs.AccountRequestDTO;
import com.example.generation.dtos.RequestDTOs.LoginRequestDTO;
import com.example.generation.entities.Account;
import com.example.generation.framework.groups.OnUpdate;
import com.example.generation.services.AccountService;
import com.example.generation.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Authorization", description = "Authorization related operations")
@RestController
@RequestMapping("/auth")
public class AuthController {
    final private AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Logs the user")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User logged in successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Account.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Account not found",
                    content = @Content
            )
    })
    public ResponseEntity<String> login(@RequestBody LoginRequestDTO loginRequestDto) {
        String token = authService.login(loginRequestDto);
        return ResponseEntity.ok(token);
    }
}