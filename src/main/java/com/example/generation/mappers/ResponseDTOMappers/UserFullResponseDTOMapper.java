package com.example.generation.mappers.ResponseDTOMappers;

import com.example.generation.dtos.ResponseDTOs.UserFullResponseDTO;
import com.example.generation.entities.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {AddressResponseDTOMapper.class})
public interface UserFullResponseDTOMapper {
    UserFullResponseDTO toDTO(User user);
}
