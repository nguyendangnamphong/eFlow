package com.vnu.uet.service.dto;

public class CheckAccessDTO {
    private String email;
    private Integer requiredRole;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public Integer getRequiredRole() { return requiredRole; }
    public void setRequiredRole(Integer requiredRole) { this.requiredRole = requiredRole; }
}
