package com.example.generation.dtos.RequestDTOs;

import com.example.generation.framework.annotations.ValidIBAN;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ATMRequestDTO extends BaseTransactionRequestDTO {
    @ValidIBAN
    private String iban;
}
