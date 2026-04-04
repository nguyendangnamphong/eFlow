package com.vnu.uet.web.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vnu.uet.IntegrationTest;
import com.vnu.uet.domain.UserProfile;
import com.vnu.uet.repository.UserProfileRepository;
import com.vnu.uet.service.PermissionManagementService;
import com.vnu.uet.service.TokenManagementService;
import com.vnu.uet.service.dto.CheckAccessDTO;
import com.vnu.uet.service.dto.VerifyCredentialsDTO;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link InternalAuthResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class InternalAuthResourceIT {

    private static final String API_URL = "/api/internal";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private TokenManagementService tokenManagementService;

    @Autowired
    private PermissionManagementService permissionManagementService;

    @Autowired
    private MockMvc restAuthMockMvc;

    private UserProfile userProfile;

    @BeforeEach
    public void initTest() {
        userProfile = new UserProfile();
        userProfile.setEmail("test@vnu.edu.vn");
        userProfile.setPassword("password123");
        userProfile = userProfileRepository.saveAndFlush(userProfile);
    }

    @Test
    @Transactional
    void generateToken() throws Exception {
        VerifyCredentialsDTO dto = new VerifyCredentialsDTO();
        dto.setEmail("test@vnu.edu.vn");
        dto.setPassword("password123");

        restAuthMockMvc
            .perform(post(API_URL + "/auth/generate-token").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(dto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").exists());
    }

    @Test
    @Transactional
    void validateToken() throws Exception {
        String token = tokenManagementService.generateToken("test@vnu.edu.vn");
        Map<String, String> request = new HashMap<>();
        request.put("token", token);

        restAuthMockMvc
            .perform(post(API_URL + "/auth/validate-token").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.isValid").value(true))
            .andExpect(jsonPath("$.email").value("test@vnu.edu.vn"));
    }

    @Test
    @Transactional
    void checkAccess() throws Exception {
        permissionManagementService.assignPermissions("test@vnu.edu.vn", java.util.List.of(1));

        CheckAccessDTO dto = new CheckAccessDTO();
        dto.setEmail("test@vnu.edu.vn");
        dto.setRequiredRole(1);

        restAuthMockMvc
            .perform(post(API_URL + "/permissions/check-access").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(dto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.hasAccess").value(true));
    }
}
