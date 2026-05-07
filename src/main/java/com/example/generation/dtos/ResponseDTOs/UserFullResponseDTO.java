package com.example.generation.dtos.ResponseDTOs;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UserFullResponseDTO {
    long id;
    String firstName;
    String lastName;
    String email;
    AddressResponseDTO address;
}
