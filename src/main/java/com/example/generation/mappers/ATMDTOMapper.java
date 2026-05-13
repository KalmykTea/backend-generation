package com.example.generation.mappers;

import com.example.generation.dtos.ATMDTO;
import com.example.generation.entities.Transaction;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ATMDTOMapper {
   Transaction toTransactionEntity(ATMDTO dto);
   ATMDTO toATMDTO(Transaction transaction);
}
