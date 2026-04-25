package com.example.generation.mappers.ResponseDTOMappers;

import com.example.generation.dtos.ResponseDTOs.UserResponseDTO;
import com.example.generation.entities.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {AddressResponseDTOMapper.class})
public interface UserResponseDTOMapper {
    UserResponseDTO toDTO(User user);
}
