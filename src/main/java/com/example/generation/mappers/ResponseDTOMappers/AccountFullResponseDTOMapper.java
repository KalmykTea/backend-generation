package com.example.generation.mappers.ResponseDTOMappers;

import com.example.generation.dtos.ResponseDTOs.AccountFullResponseDTO;
import com.example.generation.entities.Account;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccountFullResponseDTOMapper {
    AccountFullResponseDTO toDTO(Account account);
}
