package com.example.generation.dtos.RequestDTOs;

import com.example.generation.framework.annotations.ValidIBAN;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransferRequestDTO extends BaseTransactionRequestDTO {

    @ValidIBAN
    private String fromAccountIban;

    @ValidIBAN
    private String toAccountIban;
}
