package com.example.generation.dtos.ResponseDTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AddressResponseDTO {
    long id;
    String addressLine;
    String postalCode;
    String city;
    String country;
}