package com.example.generation.mappers.RequestDTOMappers;

import com.example.generation.dtos.RequestDTOs.AccountTransactionRequestDTO;
import com.example.generation.entities.Account;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AccountTransactionDTOMapper {
    @BeanMapping(ignoreByDefault = true)
    @Mapping(source = "iban", target = "iban")
    @Mapping(source = "userId", target = "user.id")
    @Mapping(source = "accountType", target = "accountType")
    Account toEntity(AccountTransactionRequestDTO accountTransactionRequestDTO);
}
