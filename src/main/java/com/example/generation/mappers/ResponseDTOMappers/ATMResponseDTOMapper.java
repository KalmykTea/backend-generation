package com.example.generation.mappers.ResponseDTOMappers;

import com.example.generation.dtos.ResponseDTOs.ATMResponseDTO;
import com.example.generation.entities.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ATMResponseDTOMapper {
    @Mapping(target="iban", source="fromAccount.iban")
    ATMResponseDTO toDTO(Transaction transaction);

}
