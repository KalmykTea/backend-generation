package com.example.generation.dtos.RequestDTOs;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ATMRequestDTO extends BaseTransactionRequestDTO {
    @NotBlank
    private String iban;
}
