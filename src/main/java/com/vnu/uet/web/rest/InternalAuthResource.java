package com.vnu.uet.web.rest;

import com.vnu.uet.service.AuthInterService;
import com.vnu.uet.service.PermissionManagementService;
import com.vnu.uet.service.TokenManagementService;
import com.vnu.uet.service.dto.CheckAccessDTO;
import com.vnu.uet.service.dto.VerifyCredentialsDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/internal")
public class InternalAuthResource {

    private final AuthInterService authInterService;
    private final PermissionManagementService permissionManagementService;
    private final TokenManagementService tokenManagementService;

    public InternalAuthResource(AuthInterService authInterService, 
                                PermissionManagementService permissionManagementService,
                                TokenManagementService tokenManagementService) {
        this.authInterService = authInterService;
        this.permissionManagementService = permissionManagementService;
        this.tokenManagementService = tokenManagementService;
    }

    @PostMapping("/auth/generate-token")
    public ResponseEntity<Map<String, Object>> generateToken(@RequestBody VerifyCredentialsDTO dto) {
        boolean verified = authInterService.verifyEmailAndPassword(dto.getEmail(), dto.getPassword());
        
        if (verified) {
            String token = tokenManagementService.generateToken(dto.getEmail());
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("roles", permissionManagementService.getUserPermissions(dto.getEmail()));
            return ResponseEntity.ok(response);
        }
        
        return ResponseEntity.status(401).build();
    }

    @PostMapping("/auth/validate-token")
    public ResponseEntity<Map<String, Object>> validateToken(@RequestBody Map<String, String> request) {
        String tokenStr = request.get("token");
        String email = tokenManagementService.getEmailFromToken(tokenStr);
        boolean isValid = tokenManagementService.validateToken(tokenStr, email);

        Map<String, Object> response = new HashMap<>();
        response.put("isValid", isValid);
        if (isValid && email != null) {
            response.put("email", email);
            response.put("roles", permissionManagementService.getUserPermissions(email));
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/permissions/check-access")
    public ResponseEntity<Map<String, Object>> checkAccess(@RequestBody CheckAccessDTO dto) {
        boolean hasAccess = permissionManagementService.hasPermission(dto.getEmail(), dto.getRequiredRole());
        
        Map<String, Object> response = new HashMap<>();
        response.put("hasAccess", hasAccess);
        return ResponseEntity.ok(response);
    }
}
