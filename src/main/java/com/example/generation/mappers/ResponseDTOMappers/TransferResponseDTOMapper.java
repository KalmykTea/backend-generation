package com.example.generation.mappers.ResponseDTOMappers;

import com.example.generation.dtos.ResponseDTOs.TransferResponseDTO;
import com.example.generation.entities.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransferResponseDTOMapper {
    @Mapping(target="fromAccountIban", source="fromAccount.iban")
    @Mapping(target="toAccountIban", source="toAccount.iban")
    TransferResponseDTO toDTO(Transaction transaction);
}
