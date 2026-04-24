package com.example.generation.mappers.RequestDTOMappers;

import com.example.generation.dtos.RequestDTOs.AccountRequestDTO;
import com.example.generation.entities.Account;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AccountRequestDTOMapper {
    @BeanMapping(ignoreByDefault = true)
    @Mapping(source = "iban", target = "iban")
    @Mapping(source = "accountType", target = "accountType")
    @Mapping(source = "absoluteLimit", target = "absoluteLimit")
    @Mapping(source = "dailyLimit", target = "dailyLimit")
    @Mapping(source = "dailyTransfer", target = "dailyTransfer")
    @Mapping(target = "user", ignore = true)
    Account toAccountEntity(AccountRequestDTO accountRequestDTO);
    AccountRequestDTO toAccountRequestDTO(Account account);
}
