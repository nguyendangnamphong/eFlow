package com.vnu.uet.web.rest;

import com.vnu.uet.service.PermissionManagementService;
import com.vnu.uet.service.dto.EmailRequestDTO;
import com.vnu.uet.service.dto.RolesRequestDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/permissions")
public class PermissionResource {

    private final PermissionManagementService permissionManagementService;

    public PermissionResource(PermissionManagementService permissionManagementService) {
        this.permissionManagementService = permissionManagementService;
    }

    @PostMapping("/search-user-roles")
    public ResponseEntity<Map<String, Object>> searchUserRoles(@RequestBody EmailRequestDTO dto) {
        Map<String, Object> response = new HashMap<>();
        response.put("email", dto.getEmail());
        response.put("roles", permissionManagementService.getUserPermissions(dto.getEmail()));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/sync")
    public ResponseEntity<Map<String, Object>> syncRoles(@RequestBody RolesRequestDTO dto) {
        permissionManagementService.assignPermissions(dto.getEmail(), dto.getRoles());
        Map<String, Object> response = new HashMap<>();
        response.put("email", dto.getEmail());
        response.put("roles", permissionManagementService.getUserPermissions(dto.getEmail()));
        response.put("message", "Đã cập nhật danh sách quyền thành công");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/system-roles")
    public ResponseEntity<List<Map<String, Object>>> getSystemRoles() {
        return ResponseEntity.ok(permissionManagementService.getSystemRoles());
    }

    @PostMapping("/revoke")
    public ResponseEntity<Void> revokeRoles(@RequestBody RolesRequestDTO dto) {
        permissionManagementService.removePermissions(dto.getEmail(), dto.getRoles());
        return ResponseEntity.ok().build();
    }
}
