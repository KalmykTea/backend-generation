package com.example.generation.controllers;

import com.example.generation.dtos.ResponseDTOs.EmployeeAccountResponseDTO;
import com.example.generation.entities.Account;
import com.example.generation.entities.User;
import com.example.generation.enums.AccountStatus;
import com.example.generation.enums.AccountType;
import com.example.generation.enums.Role;
import com.example.generation.services.AccountService;
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

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class EmployeeAccountControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AccountService accountService;

    private EmployeeAccountResponseDTO testDto;

    @BeforeEach
    void setUp() {
        testDto = new EmployeeAccountResponseDTO(
                1L,
                "NL01INHO0000000001",
                "John Doe",
                AccountType.CURRENT,
                AccountStatus.ACTIVE,
                new BigDecimal("100.00")
        );
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void getAccounts_AsEmployee_ShouldReturnPaginatedList() throws Exception {
        Page<EmployeeAccountResponseDTO> page = new PageImpl<>(List.of(testDto), PageRequest.of(0, 20), 1);
        when(accountService.getPaginatedAccounts(any())).thenReturn(page);

        mockMvc.perform(get("/api/employee/accounts?page=0&size=20"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].accountId").value(1))
                .andExpect(jsonPath("$.content[0].accountNumber").value("NL01INHO0000000001"))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void getAccounts_AsCustomer_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/api/employee/accounts"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getAccounts_Unauthenticated_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/employee/accounts"))
                .andExpect(status().isUnauthorized());
    }
}
