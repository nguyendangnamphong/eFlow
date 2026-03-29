package com.vnu.uet.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PermissionManagementService {

    // Mocking an in-memory role storage since Authority mapping requires integration with built-in User
    private final Map<String, List<Integer>> userRolesMock = new HashMap<>();

    public void assignDefaultPermission(String email) {
        List<Integer> roles = userRolesMock.getOrDefault(email, new ArrayList<>());
        if (!roles.contains(-1)) {
            roles.add(-1);
            userRolesMock.put(email, roles);
        }
    }

    public List<Integer> getUserPermissions(String email) {
         return userRolesMock.getOrDefault(email, List.of(-1));
    }

    public void assignPermissions(String email, List<Integer> permissions) {
        userRolesMock.put(email, new ArrayList<>(permissions));
    }

    public List<Map<String, Object>> getSystemRoles() {
        List<Map<String, Object>> roles = new ArrayList<>();
        roles.add(Map.of("id", -1, "name", "Người dùng hệ thống"));
        roles.add(Map.of("id", 1, "name", "Quản lý nhân sự"));
        roles.add(Map.of("id", 2, "name", "Khai báo thông tin Form"));
        roles.add(Map.of("id", 3, "name", "Duyệt thông tin Form"));
        roles.add(Map.of("id", 4, "name", "Quản lý tài khoản"));
        roles.add(Map.of("id", 5, "name", "Quản trị hệ thống (Permissions)"));
        return roles;
    }

    public void removePermissions(String email, List<Integer> permissions) {
        List<Integer> currentRoles = new ArrayList<>(getUserPermissions(email));
        for (Integer p : permissions) {
            currentRoles.remove(p);
        }
        userRolesMock.put(email, currentRoles);
    }

    public boolean isManager(String email) {
        List<Integer> roles = getUserPermissions(email);
        return roles.stream().anyMatch(role -> role > 0);
    }

    public boolean hasPermission(String email, Integer requiredPerm) {
        return getUserPermissions(email).contains(requiredPerm);
    }
}
