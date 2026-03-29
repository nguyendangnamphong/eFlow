package com.vnu.uet.service.dto;

import jakarta.validation.constraints.NotNull;
import java.time.Instant;

public class ProfileDTO {
    @NotNull(message = "Email is required")
    private String email;
    @NotNull(message = "First name is required")
    private String firstName;
    
    private String password;
    
    @NotNull(message = "Phone is required")
    private String phone;
    
    private Instant dob;
    private String gender;
    private String position;
    private String job;
    
    @NotNull(message = "Department is required")
    private String department;
    
    private String avatar;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public Instant getDob() { return dob; }
    public void setDob(Instant dob) { this.dob = dob; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    public String getJob() { return job; }
    public void setJob(String job) { this.job = job; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
}
