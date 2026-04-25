package com.example.generation.mappers.ResponseDTOMappers;

import com.example.generation.dtos.ResponseDTOs.AccountResponseDTO;
import com.example.generation.entities.Account;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccountResponseDTOMapper {
    AccountResponseDTO toDTO(Account account);
}
