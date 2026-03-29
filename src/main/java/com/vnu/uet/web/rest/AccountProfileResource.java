package com.vnu.uet.web.rest;

import com.vnu.uet.domain.UserProfile;
import com.vnu.uet.service.AccountManagementService;
import com.vnu.uet.service.UserProfileService;
import com.vnu.uet.service.dto.ProfileDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/account")
public class AccountProfileResource {

    private final UserProfileService userProfileService;
    private final AccountManagementService accountManagementService;

    public AccountProfileResource(UserProfileService userProfileService, AccountManagementService accountManagementService) {
        this.userProfileService = userProfileService;
        this.accountManagementService = accountManagementService;
    }

    // Temporary mock for current user email since Auth service is not fully integrated yet
    private String getCurrentUserEmail() {
        return "user@vnu.uet"; 
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAccount() {
        Map<String, Object> response = new HashMap<>();
        response.put("id", 159822);
        response.put("login", "kyta_ptsc@yopmail.com");
        response.put("firstName", "Tổng công ty");
        response.put("lastName", "Cổ phần Dịch vụ Kỹ thuật Dầu khí Việt Nam");
        response.put("email", "kyta_ptsc@yopmail.com");
        response.put("gender", null);
        response.put("address", "18 Phố Láng Hạ, Phường Giảng Võ, TP Hà Nội, Việt Nam.");
        response.put("imageUrl", "https://prod-2025-eaccount-user-image-second.s3-han02.fptcloud.com/user/avatar/9/P2T95P0yn3gtQP6gB8zx_159822_avatar.jpg");
        response.put("activated", true);
        response.put("langKey", "vi");
        response.put("createdBy", null);
        response.put("createdDate", "2025-08-21T03:12:06Z");
        response.put("lastModifiedBy", "kyta_ptsc@yopmail.com");
        response.put("lastModifiedDate", "2025-08-22T03:30:15Z");
        response.put("authorities", Arrays.asList("ROLE_USER", "ROLE_CUST_DOC", "ROLE_ORG_ADMIN"));
        response.put("phone", "0999999999");
        response.put("dob", null);
        response.put("avatar", "");
        response.put("signature", null);
        response.put("custId", 6306);
        response.put("orgName", "TỔNG CÔNG TY CỔ PHẦN DỊCH VỤ KỸ THUẬT DẦU KHÍ VIỆT NAM");
        response.put("custType", "O");
        response.put("parentOrgName", "TỔNG CÔNG TY CỔ PHẦN DỊCH VỤ KỸ THUẬT DẦU KHÍ VIỆT NAM");
        response.put("taxCodeOrg", "0100681592");
        response.put("acNameOrg", "TĐCNNLQGVNNLQGVN");
        response.put("orgCode", null);

        Map<String, Object> org = new HashMap<>();
        org.put("id", 14366);
        org.put("name", "TỔNG CÔNG TY CỔ PHẦN DỊCH VỤ KỸ THUẬT DẦU KHÍ VIỆT NAM");
        org.put("acName", "TĐCNNLQGVNNLQGVN");
        org.put("custId", 6306);
        org.put("folderName", "TĐCNNLQGVNNLQGVN");
        org.put("folderPath", "/storage-01/6306");
        org.put("orgCode", null);
        org.put("taxCode", "0100681592");
        org.put("parentId", null);
        org.put("orgIn", "/6306/14366");
        org.put("folderId", "14366");
        org.put("type", "LEGAL");
        org.put("createdBy", "anonymousUser");
        org.put("createdDate", "2025-08-21T03:12:06Z");
        org.put("lastModifiedBy", null);
        org.put("lastModifiedDate", null);
        org.put("activated", true);
        org.put("globalName", null);
        org.put("mainJob", null);
        org.put("issuedDate", null);
        org.put("authorizedCapital", null);
        org.put("address", null);
        org.put("storageId", null);
        org.put("blocked", false);
        org.put("existUsers", true);
        response.put("organization", org);

        response.put("subOrganizations", Arrays.asList());
        response.put("step", null);

        Map<String, Object> storage = new HashMap<>();
        storage.put("id", 49);
        storage.put("type", "tinymce");
        storage.put("content", "tinymce");
        storage.put("custId", 0);
        Map<String, Object> attrs = new HashMap<>();
        attrs.put("key", "uxmk6h6swna5ukr3rypcsjoc6n9q2ls5691c80vdjufq00ty");
        storage.put("attrs", attrs);
        storage.put("createdBy", null);
        storage.put("createdDate", null);
        storage.put("lastModifiedBy", "hieult35@fpt.com.vn");
        storage.put("lastModifiedDate", "2025-10-07T08:18:30Z");
        response.put("otherDataStorage", storage);

        response.put("activityLogName", null);
        response.put("groupId", null);
        response.put("userId", null);
        response.put("emailCustomer", null);
        response.put("features", null);
        response.put("orgIn", "/6306/14366");
        response.put("dbSuffix", "00007");
        response.put("blocked", false);
        response.put("folderPath", "/storage-01/6306");
        response.put("folderId", "0000719zlc5A1tpRgx0XAwFeqLy");
        response.put("orgId", 14366);

        Map<String, Object> ds = new HashMap<>();
        ds.put("id", null);
        ds.put("type", "TB");
        ds.put("w", 164);
        ds.put("h", 90);
        Map<String, Object> logo = new HashMap<>();
        logo.put("position", "T");
        logo.put("size", null);
        logo.put("required", true);
        logo.put("value", "");
        ds.put("fieldLogoDesign", logo);
        Map<String, Object> text = new HashMap<>();
        text.put("position", "B");
        text.put("required", true);
        text.put("items", null);
        text.put("color", "0,0,0");
        text.put("font", "TIMES_NEW_ROMAN");
        Map<String, Object> display = new HashMap<>();
        display.put("signedBy", true);
        display.put("title", true);
        display.put("signingDate", true);
        text.put("textDisplay", display);
        text.put("multiLine", true);
        ds.put("fieldTextDesign", text);
        ds.put("star", true);
        ds.put("name", "Chữ ký ảnh số");
        response.put("digitalSignature", ds);

        response.put("defaultOrgIn", null);
        response.put("bio", null);
        response.put("taxCode", null);
        response.put("knowledgeUsers", null);
        Map<String, Object> rootAttrs = new HashMap<>();
        Map<String, Object> priorityItem = new HashMap<>();
        priorityItem.put("orgIn", "/6306/14366");
        priorityItem.put("priority", 159822);
        rootAttrs.put("priority", Arrays.asList(priorityItem));
        response.put("attrs", rootAttrs);
        response.put("existedIdCard", false);
        response.put("lastModifiedPasswordDate", "2025-08-21T03:14:11Z");
        response.put("roles", Arrays.asList());
        response.put("policiesIdList", null);
        response.put("policiesList", null);
        response.put("userDTOAuthor", null);
        response.put("legalType", null);
        response.put("zoom", "1");
        response.put("groups", null);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> getProfile() {
        String email = getCurrentUserEmail();
        
        UserProfile profile = userProfileService.getUserProfileByEmail(email).orElseGet(() -> {
            // Return a mock profile if not found in DB yet for testing
            UserProfile defaultProfile = new UserProfile();
            defaultProfile.setEmail(email);
            defaultProfile.setFirstName("Nguyễn Văn A");
            defaultProfile.setPhone("0912345678");
            defaultProfile.setGender("MALE");
            defaultProfile.setPosition("Nhân viên");
            defaultProfile.setJob("Kế toán");
            defaultProfile.setDepartment("Phòng BI");
            defaultProfile.setAvatar("https://s3.cloud/eaccount/avatars/user_a.png");
            return defaultProfile;
        });

        Map<String, Object> response = new HashMap<>();
        response.put("email", profile.getEmail());
        response.put("firstName", profile.getFirstName());
        response.put("phone", profile.getPhone());
        response.put("dob", profile.getDob());
        response.put("gender", profile.getGender());
        response.put("position", profile.getPosition());
        response.put("job", profile.getJob());
        response.put("department", profile.getDepartment());
        response.put("avatar", profile.getAvatar());
        response.put("roles", Arrays.asList(-1));

        return ResponseEntity.ok(response);
    }

    @PostMapping("/profile")
    public ResponseEntity<Map<String, Object>> createProfile(@Valid @RequestBody ProfileDTO dto) {
        try {
            // Reusing account management service for HR creation logic
            String generatedPassword = accountManagementService.createEmployee(dto);
            
            Map<String, Object> response = new HashMap<>();
            response.put("email", dto.getEmail());
            response.put("generatedPassword", generatedPassword);
            response.put("message", "Tài khoản đã được tạo thành công");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<Map<String, Object>> updateProfile(@RequestBody ProfileDTO dto) {
        String email = getCurrentUserEmail(); // Or read from JWT
        dto.setEmail(email); // Override with token email for security self-update
        
        // Ensure profile exists in DB to be updated
        boolean exists = userProfileService.getUserProfileByEmail(email).isPresent();
        if (!exists) {
            UserProfile newProfile = new UserProfile();
            newProfile.setEmail(email);
            newProfile.setPhone(dto.getPhone() != null ? dto.getPhone() : "0900000000"); 
            userProfileService.saveProfile(newProfile);
        }

        try {
            UserProfile updated = userProfileService.updateProfile(email, dto);
            
            Map<String, Object> data = new HashMap<>();
            data.put("email", updated.getEmail());
            data.put("firstName", updated.getFirstName());
            data.put("phone", updated.getPhone());
            data.put("dob", updated.getDob());
            data.put("gender", updated.getGender());
            data.put("position", updated.getPosition());
            data.put("job", updated.getJob());
            data.put("department", updated.getDepartment());
            data.put("avatar", updated.getAvatar());
            data.put("roles", Arrays.asList(-1));
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Cập nhật thông tin thành công");
            response.put("data", data);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
