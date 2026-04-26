package com.example.generation.dtos.ResponseDTOs;

import lombok.Data;

@Data
public class UserResponseDTO {
    private long id;
    private AddressResponseDTO address;
    private String firstName;
    private String lastName;
    private String email;
}
