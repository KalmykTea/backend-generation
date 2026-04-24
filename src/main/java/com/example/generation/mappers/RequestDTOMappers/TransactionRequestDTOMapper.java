package com.example.generation.mappers.RequestDTOMappers;

import com.example.generation.dtos.RequestDTOs.TransactionRequestDTO;
import com.example.generation.entities.Transaction;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionRequestDTOMapper {
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "fromAccount", ignore = true)
    @Mapping(target = "toAccount", ignore = true)
    @Mapping(source = "amount", target = "amount")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "transactionType", target = "transactionType")
    Transaction toEntity(TransactionRequestDTO transactionRequestDTO);
}
