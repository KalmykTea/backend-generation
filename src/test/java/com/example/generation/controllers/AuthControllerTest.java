package com.example.generation.controllers;

import com.example.generation.dtos.RequestDTOs.AddressRequestDTO;
import com.example.generation.dtos.RequestDTOs.UserFullRequestDTO;
import com.example.generation.entities.User;
import com.example.generation.enums.Role;
import com.example.generation.enums.UserStatus;
import com.example.generation.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ObjectMapper objectMapper;
    private User testUser;
    private final String rawPassword = "password123";

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setEmail("testuser@test.com");
        testUser.setPassword(passwordEncoder.encode(rawPassword));
        testUser.setBsnNumber("999888777");
        testUser.setBirthdate(java.time.LocalDate.of(1995, 1, 1));
        testUser.setPhoneNumber("0612345000");
        testUser.setRole(Role.CUSTOMER);
        testUser.setUserStatus(UserStatus.APPROVED);
        userRepository.save(testUser);
    }

    @Test
    void login_validCredentials_returns200() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + testUser.getEmail() + "\",\"password\":\"" + rawPassword + "\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void me_withValidToken_returns200() throws Exception {
        String token = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + testUser.getEmail() + "\",\"password\":\"" + rawPassword + "\"}"))
                .andReturn().getResponse().getContentAsString();

        mockMvc.perform(get("/auth/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(testUser.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(testUser.getLastName()))
                .andExpect(jsonPath("$.role").value(testUser.getRole().name()));
    }

    @Test
    void login_wrongPassword_returns401() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + testUser.getEmail() + "\",\"password\":\"wrongpassword\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_nonExistentEmail_returns401() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"nonexist@test.com\",\"password\":\"" + rawPassword + "\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void me_withoutToken_returns403() throws Exception {
        mockMvc.perform(get("/auth/me"))
                .andExpect(status().isForbidden());
    }


    @Test
    void register_validPayload_returnsCreatedAndToken() throws Exception {
        UserFullRequestDTO requestDTO = createValidRegisterRequestDTO("new.customer@test.com");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(result -> org.junit.jupiter.api.Assertions.assertFalse(
                        result.getResponse().getContentAsString().isBlank()
                ));
    }

    @Test
    void register_invalidEmail_returnsBadRequest() throws Exception {
        UserFullRequestDTO requestDTO = createValidRegisterRequestDTO("invalid-email");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation Failed"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void register_blankFirstName_returnsBadRequest() throws Exception {
        UserFullRequestDTO requestDTO = createValidRegisterRequestDTO("blank.firstname@test.com");
        requestDTO.setFirstName("");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation Failed"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void register_duplicateEmail_returnsConflict() throws Exception {
        UserFullRequestDTO requestDTO = createValidRegisterRequestDTO("customer@test.com");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    private UserFullRequestDTO createValidRegisterRequestDTO(String email) {
        AddressRequestDTO addressDTO = new AddressRequestDTO();
        addressDTO.setAddressLine("Test Street 1");
        addressDTO.setPostalCode("1234AB");
        addressDTO.setCity("Amsterdam");
        addressDTO.setCountry("Netherlands");

        UserFullRequestDTO requestDTO = new UserFullRequestDTO();
        requestDTO.setAddress(addressDTO);
        requestDTO.setFirstName("Test");
        requestDTO.setLastName("Customer");
        requestDTO.setEmail(email);
        requestDTO.setPassword("Password123!");
        requestDTO.setBsnNumber("123456782");
        requestDTO.setBirthdate(LocalDate.now().minusYears(25));
        requestDTO.setPhoneNumber("0612345678");

        return requestDTO;
    }
}
