package com.bank.banking.entity;

import jakarta.persistence.*;

@Entity
public class OfficialRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    private String status; // PENDING / APPROVED / REJECTED

    // ===== GETTERS & SETTERS =====

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }
 
    public void setUsername(String username) {
        this.username = username;
    }
 
    public String getPassword() {
        return password;
    }
 
    public void setPassword(String password) {
        this.password = password;
    }
 
    public String getStatus() {
        return status;
    }
 
    public void setStatus(String status) {
        this.status = status;
    }
}
