package com.example.generation.mappers.ResponseDTOMappers;

import com.example.generation.dtos.ResponseDTOs.TransactionResponseDTO;
import com.example.generation.entities.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionResponseDTOMapper {
    @Mapping(target="fromAccountIban", source="fromAccount.iban")
    @Mapping(target="toAccountIban", source="toAccount.iban")
    TransactionResponseDTO toDTO(Transaction transaction);
}
