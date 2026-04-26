package com.example.generation.controllers;

import com.example.generation.services.AddressService;
import com.example.generation.services.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Users", description = "Operations for managing users")
@RestController
@RequestMapping("users")
public class UserController {
    final private UserService userService;
    final private AddressService addressService;

    public UserController(UserService userService, AddressService addressService) {
        this.userService = userService;
        this.addressService = addressService;
    }

    // controller methods based on user stories with swagger doc code go here
}
