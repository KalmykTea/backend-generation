package com.example.generation.controllers;

import com.example.generation.dtos.RequestDTOs.UserRequestDTO;
import com.example.generation.dtos.ResponseDTOs.UserResponseDTO;
import com.example.generation.entities.User;
import com.example.generation.framework.groups.OnCreate;
import com.example.generation.mappers.RequestDTOMappers.UserRequestDTOMapper;
import com.example.generation.mappers.ResponseDTOMappers.UserResponseDTOMapper;
import com.example.generation.services.AddressService;
import com.example.generation.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Users", description = "Operations for managing users")
@RestController
@RequestMapping("users")
public class UserController {
    private final UserService userService;
    private final AddressService addressService;
    private final UserRequestDTOMapper userRequestDTOMapper;
    private final UserResponseDTOMapper userResponseDTOMapper;

    public UserController(UserService userService, AddressService addressService, UserRequestDTOMapper userRequestDTOMapper, UserResponseDTOMapper userResponseDTOMapper) {
        this.userService = userService;
        this.addressService = addressService;
        this.userRequestDTOMapper = userRequestDTOMapper;
        this.userResponseDTOMapper = userResponseDTOMapper;
    }

    @Operation(summary = "Register a new customer")
    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@Validated(OnCreate.class) @RequestBody UserRequestDTO userRequestDTO) {
        User user = userRequestDTOMapper.toEntity(userRequestDTO);
        User registeredUser = userService.register(user);
        return new ResponseEntity<>(userResponseDTOMapper.toDTO(registeredUser), HttpStatus.CREATED);
    }

    // controller methods based on user stories with swagger doc code go here
}
