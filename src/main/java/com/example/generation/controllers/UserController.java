package com.example.generation.controllers;

import com.example.generation.dtos.RequestDTOs.AccountLimitsRequestDTO;
import com.example.generation.dtos.RequestDTOs.UserFullRequestDTO;
import com.example.generation.dtos.ResponseDTOs.UserFullResponseDTO;
import com.example.generation.dtos.ResponseDTOs.UserResponseDTO;
import com.example.generation.entities.User;
import com.example.generation.framework.groups.OnCreate;
import com.example.generation.mappers.RequestDTOMappers.UserFullRequestDTOMapper;
import com.example.generation.mappers.ResponseDTOMappers.UserFullResponseDTOMapper;
import com.example.generation.mappers.ResponseDTOMappers.UserResponseDTOMapper;
import com.example.generation.services.AddressService;
import com.example.generation.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
    private final UserService userService;
    private final AddressService addressService;
    private final UserFullRequestDTOMapper userRequestDTOMapper;
    private final UserResponseDTOMapper userResponseDTOMapper;
    private final UserFullResponseDTOMapper userFullResponseDTOMapper;

    public UserController(UserService userService, AddressService addressService, UserFullRequestDTOMapper userRequestDTOMapper, UserResponseDTOMapper userResponseDTOMapper, UserFullResponseDTOMapper userFullResponseDTOMapper) {
        this.userService = userService;
        this.addressService = addressService;
        this.userRequestDTOMapper = userRequestDTOMapper;
        this.userResponseDTOMapper = userResponseDTOMapper;
        this.userFullResponseDTOMapper = userFullResponseDTOMapper;
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
    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAuthority('EMPLOYEE')")
    public ResponseEntity<UserResponseDTO> approveUser(@PathVariable Long id,
                                                       @RequestBody @Validated(OnCreate.class)
                                                       List<@Valid AccountLimitsRequestDTO> accountLimitsRequestDTOS) {
        UserResponseDTO result = userService.approveUser(id, accountLimitsRequestDTOS);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(summary = "Get user by ID")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('EMPLOYEE') or @permissionEvaluator.isOwner(authentication, #id)")
    public ResponseEntity<UserFullResponseDTO> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return new ResponseEntity<>(userFullResponseDTOMapper.toDTO(user), HttpStatus.OK);
    }

    @Operation(summary = "Get all users")
    @GetMapping
    @PreAuthorize("hasAuthority('EMPLOYEE')")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
    }
}
