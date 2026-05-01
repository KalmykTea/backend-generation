package com.example.generation.controllers;

import com.example.generation.dtos.ResponseDTOs.UserResponseDTO;
import com.example.generation.entities.User;
import com.example.generation.enums.UserStatus;
import com.example.generation.mappers.ResponseDTOMappers.UserResponseDTOMapper;
import com.example.generation.services.AddressService;
import com.example.generation.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Users", description = "Operations for managing users")
@RestController
@RequestMapping("users")
public class UserController {
    final private UserService userService;
    final private AddressService addressService;
    final private UserResponseDTOMapper userResponseDTOMapper;

    public UserController(UserService userService, AddressService addressService, UserResponseDTOMapper userResponseDTOMapper) {
        this.userService = userService;
        this.addressService = addressService;
        this.userResponseDTOMapper = userResponseDTOMapper;
    }

    // controller methods based on user stories with swagger doc code go here

    @Operation(summary = "Get list of customers pending approval")
    @GetMapping("/pending")
    public ResponseEntity<List<UserResponseDTO>> getPendingUsers() {
        List<User> pendingUsers = userService.findByUserStatus(UserStatus.PENDING);
        List<UserResponseDTO> result = pendingUsers.stream().map(userResponseDTOMapper::toDTO).toList();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
