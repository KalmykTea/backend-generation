package com.example.generation.mappers.ResponseDTOMappers;

import com.example.generation.dtos.ResponseDTOs.ATMResponseDTO;
import com.example.generation.entities.Transaction;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ATMResponseDTOMapper {
   ATMResponseDTO toDTO(Transaction transaction);
}
