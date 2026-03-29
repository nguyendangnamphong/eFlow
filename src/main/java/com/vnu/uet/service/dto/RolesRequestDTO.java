package com.vnu.uet.service.dto;

import java.util.List;

public class RolesRequestDTO {
    private String email;
    private List<Integer> roles;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public List<Integer> getRoles() { return roles; }
    public void setRoles(List<Integer> roles) { this.roles = roles; }
}
