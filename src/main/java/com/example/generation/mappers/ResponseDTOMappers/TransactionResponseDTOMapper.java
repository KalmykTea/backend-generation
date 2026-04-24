package com.example.generation.mappers.ResponseDTOMappers;

import com.example.generation.dtos.ResponseDTOs.TransactionResponseDTO;
import com.example.generation.entities.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionResponseDTOMapper {
    @Mapping(source = "fromAccount.iban", target = "fromAccountIBAN")
    @Mapping(source = "toAccount.iban", target = "toAccountIBAN")
    @Mapping(source = "fromAccount.user.firstName", target = "fromAccountFirstName")
    @Mapping(source = "toAccount.user.lastName", target = "toAccountLastName")
    TransactionResponseDTO toDTO(Transaction transaction);
}
