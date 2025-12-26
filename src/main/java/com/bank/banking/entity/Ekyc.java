package com.bank.banking.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ekyc")
public class Ekyc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    private String aadhaarNumber;
    private String panNumber;

    private String aadhaarFilePath;
    private String panFilePath;
    private String addressProofPath;

    private String status; // SUBMITTED, VERIFIED, REJECTED

    private LocalDateTime createdAt = LocalDateTime.now();

    // getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getAadhaarNumber() {
        return aadhaarNumber;
    }

    public void setAadhaarNumber(String aadhaarNumber) {
        this.aadhaarNumber = aadhaarNumber;
    }

    public String getPanNumber() {
        return panNumber;
    }

    public void setPanNumber(String panNumber) {
        this.panNumber = panNumber;
    }

    public String getAadhaarFilePath() {
        return aadhaarFilePath;
    }

    public void setAadhaarFilePath(String aadhaarFilePath) {
        this.aadhaarFilePath = aadhaarFilePath;
    }

    public String getPanFilePath() {
        return panFilePath;
    }

    public void setPanFilePath(String panFilePath) {
        this.panFilePath = panFilePath;
    }

    public String getAddressProofPath() {
        return addressProofPath;
    }

    public void setAddressProofPath(String addressProofPath) {
        this.addressProofPath = addressProofPath;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
