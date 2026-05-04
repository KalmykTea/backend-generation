package com.example.generation.mappers.RequestDTOMappers;

import com.example.generation.dtos.RequestDTOs.UserRequestDTO;
import com.example.generation.entities.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserRequestDTOMapper {
    @BeanMapping(ignoreByDefault = true)
    @Mapping(source = "id", target = "id")
    @Mapping(source = "firstName", target = "firstName")
    @Mapping(source = "lastName", target = "lastName")
    UserRequestDTO toDTO(User user);
}
