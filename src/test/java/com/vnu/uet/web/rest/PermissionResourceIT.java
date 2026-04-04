package com.vnu.uet.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vnu.uet.IntegrationTest;
import com.vnu.uet.service.PermissionManagementService;
import com.vnu.uet.service.dto.EmailRequestDTO;
import com.vnu.uet.service.dto.RolesRequestDTO;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link PermissionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PermissionResourceIT {

    private static final String API_URL = "/api/permissions";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PermissionManagementService permissionManagementService;

    @Autowired
    private MockMvc restPermissionMockMvc;

    @Test
    @Transactional
    void searchUserRoles() throws Exception {
        EmailRequestDTO dto = new EmailRequestDTO();
        dto.setEmail("user@vnu.edu.vn");

        restPermissionMockMvc
            .perform(post(API_URL + "/search-user-roles").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(dto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value("user@vnu.edu.vn"))
            .andExpect(jsonPath("$.roles").isArray());
    }

    @Test
    @Transactional
    void syncRoles() throws Exception {
        RolesRequestDTO dto = new RolesRequestDTO();
        dto.setEmail("user-sync@vnu.edu.vn");
        dto.setRoles(Arrays.asList(1, 2));

        restPermissionMockMvc
            .perform(post(API_URL + "/sync").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(dto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.roles").value(hasItem(1)));

        assertThat(permissionManagementService.getUserPermissions("user-sync@vnu.edu.vn")).contains(1, 2);
    }

    @Test
    @Transactional
    void getSystemRoles() throws Exception {
        restPermissionMockMvc.perform(get(API_URL + "/system-roles")).andExpect(status().isOk()).andExpect(jsonPath("$.[*]").isArray());
    }

    @Test
    @Transactional
    void revokeRoles() throws Exception {
        permissionManagementService.assignPermissions("user-revoke@vnu.edu.vn", Arrays.asList(1, 2));

        RolesRequestDTO dto = new RolesRequestDTO();
        dto.setEmail("user-revoke@vnu.edu.vn");
        dto.setRoles(List.of(1));

        restPermissionMockMvc
            .perform(post(API_URL + "/revoke").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(dto)))
            .andExpect(status().isOk());

        assertThat(permissionManagementService.getUserPermissions("user-revoke@vnu.edu.vn")).doesNotContain(1);
        assertThat(permissionManagementService.getUserPermissions("user-revoke@vnu.edu.vn")).contains(2);
    }
}
