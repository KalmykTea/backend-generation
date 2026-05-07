package com.example.generation.mappers.RequestDTOMappers;

import com.example.generation.dtos.RequestDTOs.AccountFullRequestDTO;
import com.example.generation.entities.Account;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AccountFullRequestDTOMapper {
    @BeanMapping(ignoreByDefault = true)
    @Mapping(source = "iban", target = "iban")
    @Mapping(source = "accountType", target = "accountType")
    @Mapping(source = "absoluteLimit", target = "absoluteLimit")
    @Mapping(source = "dailyLimit", target = "dailyLimit")
    @Mapping(source = "dailyTransfer", target = "dailyTransfer")
    @Mapping(source = "userId", target = "user.id")
    Account toEntity(AccountFullRequestDTO accountRequestDTO);
}
