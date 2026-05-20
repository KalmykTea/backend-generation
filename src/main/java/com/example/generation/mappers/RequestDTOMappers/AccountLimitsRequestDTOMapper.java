package com.example.generation.mappers.RequestDTOMappers;

import com.example.generation.dtos.RequestDTOs.AccountLimitsRequestDTO;
import com.example.generation.entities.Account;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AccountLimitsRequestDTOMapper {
    Account toEntity(AccountLimitsRequestDTO accountRequestDTO);
}
