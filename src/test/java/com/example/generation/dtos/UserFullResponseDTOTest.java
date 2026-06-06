package com.example.generation.dtos;

import com.example.generation.dtos.ResponseDTOs.AddressResponseDTO;
import com.example.generation.dtos.ResponseDTOs.UserFullResponseDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserFullResponseDTOTest {

    @Test
    void UserFullResponseDTO_BuilderAndGettersWork() {
        long id = 1L;
        String firstName = "John";
        String lastName = "Doe";
        String email = "john.doe@example.com";
        String phone = "0612345678";
        String bsn = "123456782";
        AddressResponseDTO address = AddressResponseDTO.builder().id(1L).addressLine("Street 1").build();

        UserFullResponseDTO dto = UserFullResponseDTO.builder()
                .id(id)
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .phoneNumber(phone)
                .bsnNumber(bsn)
                .address(address)
                .build();

        assertEquals(id, dto.getId());
        assertEquals(firstName, dto.getFirstName());
        assertEquals(lastName, dto.getLastName());
        assertEquals(email, dto.getEmail());
        assertEquals(phone, dto.getPhoneNumber());
        assertEquals(bsn, dto.getBsnNumber());
        assertEquals(address, dto.getAddress());
    }
}
