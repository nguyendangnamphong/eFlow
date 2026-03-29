package com.vnu.uet.web.rest;

import com.vnu.uet.domain.UserProfile;
import com.vnu.uet.service.AccountManagementService;
import com.vnu.uet.service.PermissionManagementService;
import com.vnu.uet.service.dto.EmailRequestDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/management/account")
public class ManagementAccountResource {

    private final AccountManagementService accountManagementService;
    private final PermissionManagementService permissionManagementService;

    public ManagementAccountResource(AccountManagementService accountManagementService, 
                                     PermissionManagementService permissionManagementService) {
        this.accountManagementService = accountManagementService;
        this.permissionManagementService = permissionManagementService;
    }

    // Mock requester email assuming JWT decoding layer isn't attached yet
    private String getRequesterEmail() {
        return "admin@vnu.uet";
    }

    @PostMapping("/search")
    public ResponseEntity<Map<String, Object>> searchAccount(@RequestBody EmailRequestDTO dto) {
        return accountManagementService.searchUserBasicInfo(dto.getEmail())
            .map(p -> {
                Map<String, Object> response = new HashMap<>();
                response.put("email", p.getEmail());
                response.put("firstName", p.getFirstName());
                response.put("phone", p.getPhone());
                response.put("department", p.getDepartment());
                response.put("isActive", true);
                response.put("roles", permissionManagementService.getUserPermissions(p.getEmail()));
                return ResponseEntity.ok(response);
            })
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/check-deletable")
    public ResponseEntity<Map<String, Object>> checkDeletable(@RequestBody EmailRequestDTO dto) {
        boolean deletable = accountManagementService.checkIfDeletable(dto.getEmail(), getRequesterEmail());
        Map<String, Object> response = new HashMap<>();
        response.put("deletable", deletable);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/delete")
    public ResponseEntity<Map<String, Object>> deleteAccount(@RequestBody EmailRequestDTO dto) {
        if (accountManagementService.checkIfDeletable(dto.getEmail(), getRequesterEmail())) {
            accountManagementService.deleteUserAccount(dto.getEmail());
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Tài khoản đã được xóa thành công");
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(403).build(); // Forbidden
    }
}
