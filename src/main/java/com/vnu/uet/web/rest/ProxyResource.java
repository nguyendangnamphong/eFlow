package com.vnu.uet.web.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for proxying requests to other microservices (eAccount, eForm).
 */
@RestController
@RequestMapping("/api/proxy")
public class ProxyResource {

    private final Logger log = LoggerFactory.getLogger(ProxyResource.class);

    /**
     * {@code GET  /eaccount/users} : Search users from eAccount.
     */
    @GetMapping("/eaccount/users")
    public ResponseEntity<List<Map<String, Object>>> searchUsers(@RequestParam(name = "query", required = false) String query) {
        log.debug("REST request to search users from eAccount with query : {}", query);

        // Mocking response from eAccount
        List<Map<String, Object>> users = new ArrayList<>();
        Map<String, Object> user1 = new HashMap<>();
        user1.put("id", "user1@vnu.edu.vn");
        user1.put("name", "Nguyen Van A");
        user1.put("email", "user1@vnu.edu.vn");
        users.add(user1);

        Map<String, Object> user2 = new HashMap<>();
        user2.put("id", "user2@vnu.edu.vn");
        user2.put("name", "Tran Thi B");
        user2.put("email", "user2@vnu.edu.vn");
        users.add(user2);

        return ResponseEntity.ok(users);
    }

    /**
     * {@code GET  /eform/published-forms} : Get published forms from eForm.
     */
    @GetMapping("/eform/published-forms")
    public ResponseEntity<List<Map<String, Object>>> getPublishedForms() {
        log.debug("REST request to get published forms from eForm");

        // Mocking response from eForm
        List<Map<String, Object>> forms = new ArrayList<>();
        Map<String, Object> form1 = new HashMap<>();
        form1.put("formId", "form-001");
        form1.put("formName", "Don xin nghi phep");
        forms.add(form1);

        Map<String, Object> form2 = new HashMap<>();
        form2.put("formId", "form-002");
        form2.put("formName", "Phieu thanh toan");
        form