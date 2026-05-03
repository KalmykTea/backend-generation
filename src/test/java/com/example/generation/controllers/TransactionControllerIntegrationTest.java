package com.example.generation.controllers;

import com.example.generation.dtos.ResponseDTOs.TransactionSummaryResponse;
import com.example.generation.enums.Role;
import com.example.generation.enums.TransactionType;
import com.example.generation.services.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TransactionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TransactionService transactionService;

    private TransactionSummaryResponse testDto;

    @BeforeEach
    void setUp() {
        TransactionSummaryResponse.InitiatedByDTO initiatedBy = new TransactionSummaryResponse.InitiatedByDTO(
                2L,
                "Customer One",
                Role.CUSTOMER
        );

        testDto = new TransactionSummaryResponse(
                1L,
                TransactionType.TRANSFER,
                "NL01INHO0000000001",
                "NL01INHO0000000002",
                new BigDecimal("100.00"),
                LocalDateTime.now(),
                initiatedBy
        );
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void getTransactions_AsEmployee_ShouldReturnPaginatedList() throws Exception {
        Page<TransactionSummaryResponse> page = new PageImpl<>(List.of(testDto), PageRequest.of(0, 20), 1);
        when(transactionService.getPaginatedTransactions(any())).thenReturn(page);

        mockMvc.perform(get("/api/employee/transactions?page=0&size=20"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].transactionId").value(1))
                .andExpect(jsonPath("$.content[0].type").value("TRANSFER"))
                .andExpect(jsonPath("$.content[0].fromAccount").value("NL01INHO0000000001"))
                .andExpect(jsonPath("$.content[0].toAccount").value("NL01INHO0000000002"))
                .andExpect(jsonPath("$.content[0].initiatedBy.fullName").value("Customer One"))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void getTransactions_AsCustomer_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/api/employee/transactions"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getTransactions_Unauthenticated_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/employee/transactions"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "customer@example.com", roles = "CUSTOMER")
    void getCustomerTransactions_AsCustomer_ShouldReturnFilteredList() throws Exception {
        Page<TransactionSummaryResponse> page = new PageImpl<>(List.of(testDto), PageRequest.of(0, 20), 1);
        when(transactionService.getFilteredTransactionsForCustomer(any(), any(), any())).thenReturn(page);

        mockMvc.perform(get("/api/customer/transactions?page=0&size=20&amountLt=500&iban=NL01INHO0000000001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].transactionId").value(1))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @WithMockUser(username = "customer@example.com", roles = "CUSTOMER")
    void getCustomerTransactions_WithConflictingAmountFilters_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/api/customer/transactions?amountEq=100&amountLt=500"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Cannot combine amountEq with amountLt or amountGt"));
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void getCustomerTransactions_AsEmployee_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/api/customer/transactions"))
                .andExpect(status().isForbidden());
    }
}
