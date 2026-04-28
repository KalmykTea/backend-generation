package com.example.generation.mappers.RequestDTOMappers;

import com.example.generation.dtos.RequestDTOs.TransactionRequestDTO;
import com.example.generation.entities.Transaction;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserRequestDTOMapper.class, AccountRequestDTOMapper.class})
public interface TransactionRequestDTOMapper {
    @BeanMapping(ignoreByDefault = true)
    @Mapping(source = "fromAccount", target = "fromAccount")
    @Mapping(source = "toAccount", target = "toAccount")
    @Mapping(source = "initiatedBy", target = "initiatedBy")
    @Mapping(source = "amount", target = "amount")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "transactionType", target = "transactionType")
    Transaction toEntity(TransactionRequestDTO transactionRequestDTO);
}
