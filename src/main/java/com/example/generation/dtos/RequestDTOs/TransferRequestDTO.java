package com.example.generation.dtos.RequestDTOs;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransferRequestDTO extends BaseTransactionRequestDTO {

    @Valid
    private AccountTransactionRequestDTO fromAccount;

    @Valid
    private AccountTransactionRequestDTO toAccount;
}
