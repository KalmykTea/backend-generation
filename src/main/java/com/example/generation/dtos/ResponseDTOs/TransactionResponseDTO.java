package com.example.generation.dtos.ResponseDTOs;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionResponseDTO {
    private long id;
    private String fromAccountFirstName;
    private String fromAccountLastName;
    private String fromAccountIBAN;
    private String toAccountFirstName;
    private String toAccountLastName;
    private String toAccountIBAN;
    private String initiatedByFirstName;
    private String initiatedByLastName;
    private String initiatedByIBAN;
    private BigDecimal amount;
    private String description;
    private LocalDateTime timestamp;
}
