package com.example.generation.dtos.RequestDTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRequestDTO {
    @NotNull
    private Long id;
    @NotBlank
    @Size(min = 1, max = 50)
    @Pattern(regexp = "^[\\p{L}\\s\\-']+$", message = "Firstname contains invalid characters")
    private String firstName;
    @NotBlank
    @Size(min = 1, max = 50)
    @Pattern(regexp = "^[\\p{L}\\s\\-']+$", message = "Firstname contains invalid characters")
    private String lastName;
}
