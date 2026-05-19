package com.example.generation.mappers.ResponseDTOMappers;

import com.example.generation.dtos.ResponseDTOs.TransferResponseDTO;
import com.example.generation.entities.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {AccountTransactionResponseDTOMapper.class, UserResponseDTOMapper.class})
public interface TransferResponseDTOMapper {
    @Mapping(source = "fromAccount.user.id", target = "fromAccount.userId")
    @Mapping(source = "toAccount.user.id", target = "toAccount.userId")
    @Mapping(source = "initiatedBy", target = "initiatedBy")
    TransferResponseDTO toDTO(Transaction transaction);
}
