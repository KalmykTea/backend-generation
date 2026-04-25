package com.example.generation.mappers.ResponseDTOMappers;

import com.example.generation.dtos.ResponseDTOs.AddressResponseDTO;
import com.example.generation.entities.Address;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AddressResponseDTOMapper {
    AddressResponseDTO toDTO(Address address);
}
