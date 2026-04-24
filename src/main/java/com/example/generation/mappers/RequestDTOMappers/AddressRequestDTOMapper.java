package com.example.generation.mappers.RequestDTOMappers;

import com.example.generation.dtos.RequestDTOs.AddressRequestDTO;
import com.example.generation.entities.Address;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AddressRequestDTOMapper {
    @BeanMapping(ignoreByDefault = true)
    @Mapping(source = "addressLine", target = "addressLine")
    @Mapping(source = "postalCode", target = "postalCode")
    @Mapping(source = "city", target = "city")
    @Mapping(source = "country", target = "country")
    Address toEntity(AddressRequestDTO addressRequestDTO);
    AddressRequestDTO toDTO(Address address);
}
