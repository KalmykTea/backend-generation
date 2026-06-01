package com.example.generation.mappers.RequestDTOMappers;

import com.example.generation.dtos.RequestDTOs.UserFullRequestDTO;
import com.example.generation.entities.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {AddressRequestDTOMapper.class})
public interface UserFullRequestDTOMapper {
    @BeanMapping(ignoreByDefault = true)
    @Mapping(source = "id", target = "id")
    @Mapping(source = "firstName", target = "firstName")
    @Mapping(source = "lastName", target = "lastName")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "password", target = "password")
    @Mapping(source = "bsnNumber", target = "bsnNumber")
    @Mapping(source = "birthdate", target = "birthdate")
    @Mapping(source = "phoneNumber", target = "phoneNumber")
    @Mapping(source = "address", target = "address")
    User toEntity(UserFullRequestDTO userFullRequestDTO);
}
