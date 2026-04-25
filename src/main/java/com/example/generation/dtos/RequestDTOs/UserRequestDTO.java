package com.example.generation.dtos.RequestDTOs;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserRequestDTO {

    @Valid
    private AddressRequestDTO address;

    @NotBlank
    @Size(min = 1, max = 50)
    @Pattern(regexp = "^[\\p{L}\\s\\-']+$", message = "Firstname contains invalid characters")
    private String firstName;

    @NotBlank
    @Size(min = 1, max = 100)
    @Pattern(regexp = "^[\\p{L}\\s\\-']+$", message = "Lastname contains invalid characters")
    private String lastName;

    @Email(message = "Please provide a valid email address")
    private String email;

    @NotBlank
    @Size(min = 8, max = 128)
    private String password;

    @Pattern(regexp = "^\\d{8,9}$", message = "BSN must be 8 or 9 digits")
    private String bsnNumber;

    @Past(message = "Birthdate must be in the past")
    private LocalDate birthdate;

    // Allowed spaces and hyphens for better UX
    @Pattern(regexp = "^0[1-9][0-9]{1,2}[- ]?\\d{6,7}$", message = "Invalid Dutch phone number")
    private String phoneNumber;
}
