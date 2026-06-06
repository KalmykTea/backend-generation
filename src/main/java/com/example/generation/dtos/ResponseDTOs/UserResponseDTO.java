package com.example.generation.dtos.ResponseDTOs;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UserResponseDTO {
    long id;
    String firstName;
    String lastName;

    String role;
    String status;
}
