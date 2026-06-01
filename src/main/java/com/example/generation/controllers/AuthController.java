package com.example.generation.controllers;

import com.example.generation.dtos.RequestDTOs.LoginRequestDTO;
import com.example.generation.dtos.RequestDTOs.UserFullRequestDTO;
import com.example.generation.dtos.ResponseDTOs.UserResponseDTO;
import com.example.generation.entities.Account;
import com.example.generation.entities.User;
import com.example.generation.framework.groups.OnCreate;
import com.example.generation.mappers.RequestDTOMappers.UserFullRequestDTOMapper;
import com.example.generation.mappers.ResponseDTOMappers.UserResponseDTOMapper;
import com.example.generation.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Tag(name = "Authorization", description = "Authorization related operations")
@RestController
@RequestMapping("/auth")
public class AuthController {
    final private AuthService authService;
    private final UserResponseDTOMapper userResponseDTOMapper;
    private final UserFullRequestDTOMapper userFullRequestDTOMapper;

    public AuthController(AuthService authService, UserResponseDTOMapper userResponseDTOMapper, UserFullRequestDTOMapper userFullRequestDTOMapper) {
        this.authService = authService;
        this.userResponseDTOMapper = userResponseDTOMapper;
        this.userFullRequestDTOMapper = userFullRequestDTOMapper;
    }

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Logs the user")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User logged in successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content
            )
    })
    public ResponseEntity<String> login(@RequestBody LoginRequestDTO loginRequestDto) {
        String token = authService.login(loginRequestDto);
        return ResponseEntity.ok(token);
    }

    @GetMapping("/me")
    UserResponseDTO me() {
        Authentication authentication = org.springframework.security.core.context.SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof User user)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }

        return this.userResponseDTOMapper.toDTO(user);
    }

    @Operation(summary = "Register a new customer")
    @PostMapping("/register")
    public ResponseEntity<String> register(@Validated(OnCreate.class) @RequestBody UserFullRequestDTO userRequestDTO) {
        User user = userFullRequestDTOMapper.toEntity(userRequestDTO);
        String token = authService.register(user);
        return new ResponseEntity<>(token, HttpStatus.CREATED);
    }
}