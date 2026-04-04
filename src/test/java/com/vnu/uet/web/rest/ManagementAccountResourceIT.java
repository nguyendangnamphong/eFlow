package com.vnu.uet.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vnu.uet.IntegrationTest;
import com.vnu.uet.domain.UserProfile;
import com.vnu.uet.repository.UserProfileRepository;
import com.vnu.uet.service.dto.EmailRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ManagementAccountResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ManagementAccountResourceIT {

    private static final String API_URL = "/api/management/account";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private MockMvc restManagementMockMvc;

    @BeforeEach
    public void initTest() {
        UserProfile user = new UserProfile();
        user.setEmail("test-management@vnu.edu.vn");
        user.setFirstName("Test");
        user.setPhone("0000000000");
        userProfileRepository.saveAndFlush(user);
    }

    @Test
    @Transactional
    void searchAccount() throws Exception {
        EmailRequestDTO dto = new EmailRequestDTO();
        dto.setEmail("test-management@vnu.edu.vn");

        restManagementMockMvc
            .perform(post(API_URL + "/search").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(dto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value("test-management@vnu.edu.vn"));
    }

    @Test
    @Transactional
    void checkDeletable() throws Exception {
        EmailRequestDTO dto = new EmailRequestDTO();
        dto.setEmail("test-management@vnu.edu.vn");

        restManagementMockMvc
            .perform(post(API_URL + "/check-deletable").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(dto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.deletable").exists());
    }

    @Test
    @Transactional
    void deleteAccount() throws Exception {
        EmailRequestDTO dto = new EmailRequestDTO();
        dto.setEmail("test-management@vnu.edu.vn");

        restManagementMockMvc
            .perform(post(API_URL + "/delete").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(dto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("success"));

        assertThat(userProfileRepository.findOneByEmail("test-management@vnu.edu.vn")).isNotPresent();
    }
}
