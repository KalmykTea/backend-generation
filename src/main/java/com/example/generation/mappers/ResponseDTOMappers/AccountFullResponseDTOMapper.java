package com.example.generation.mappers.ResponseDTOMappers;

import com.example.generation.dtos.ResponseDTOs.AccountFullResponseDTO;
import com.example.generation.entities.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AccountFullResponseDTOMapper {
    @Mapping(source = "accountStatus", target = "accountStatus")
    AccountFullResponseDTO toDTO(Account account);
}
