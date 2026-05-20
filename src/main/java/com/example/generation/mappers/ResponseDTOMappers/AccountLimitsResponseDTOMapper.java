package com.example.generation.mappers.ResponseDTOMappers;

import com.example.generation.dtos.ResponseDTOs.AccountLimitsResponseDTO;
import com.example.generation.entities.Account;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccountLimitsResponseDTOMapper {
    AccountLimitsResponseDTO toDTO(Account account);
}
