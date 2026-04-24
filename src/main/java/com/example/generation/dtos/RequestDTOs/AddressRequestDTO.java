package com.example.generation.dtos.RequestDTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AddressRequestDTO {
    @NotBlank
    @Size(min = 1, max = 128)
    private String addressLine;
    @NotBlank
    @Size(min = 4, max = 10, message = "Postal code length is invalid")
    private String postalCode;
    @NotBlank
    @Size(min = 1, max = 60)
    private String city;
    @NotBlank
    @Size(min = 1, max = 60)
    private String country;
}
