package com.example.generation.mappers.ResponseDTOMappers;

import com.example.generation.dtos.ResponseDTOs.AccountTransactionResponseDTO;
import com.example.generation.entities.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AccountTransactionResponseDTOMapper {
    @Mapping(source = "user.id", target = "userId")
    AccountTransactionResponseDTO toDTO(Account account);
}
