package com.vnu.uet.web.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vnu.uet.IntegrationTest;
import com.vnu.uet.repository.UserProfileRepository;
import com.vnu.uet.service.dto.ProfileDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link AccountProfileResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class AccountProfileResourceIT {

    private static final String API_URL = "/api/account";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private MockMvc restAccountMockMvc;

    @BeforeEach
    public void initTest() {
        // Clear or setup data if needed
    }

    @Test
    @Transactional
    void getAccount() throws Exception {
        restAccountMockMvc.perform(get(API_URL)).andExpect(status().isOk()).andExpect(jsonPath("$.email").value("kyta_ptsc@yopmail.com"));
    }

    @Test
    @Transactional
    void getProfile() throws Exception {
        restAccountMockMvc
            .perform(get(API_URL + "/profile"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value("user@vnu.uet"));
    }

    @Test
    @Transactional
    void createProfile() throws Exception {
        ProfileDTO dto = new ProfileDTO();
        dto.setEmail("newuser@vnu.uet");
        dto.setFirstName("New");
        dto.setDepartment("IT"); // Added missing @NotNull field
        dto.setPhone("0123456789"); // Added missing @NotNull field

        restAccountMockMvc
            .perform(post(API_URL + "/profile").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(dto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value("newuser@vnu.uet"))
            .andExpect(jsonPath("$.generatedPassword").exists());
    }

    @Test
    @Transactional
    void updateProfile() throws Exception {
        ProfileDTO dto = new ProfileDTO();
        dto.setFirstName("Updated Name");
        dto.setPhone("0987654321");

        restAccountMockMvc
            .perform(put(API_URL + "/profile").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(dto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.firstName").value("Updated Name"));
    }
}
