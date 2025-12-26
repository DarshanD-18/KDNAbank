package com.bank.banking.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private LocalDate dob;
    private String gender;
    private String mobileNumber;
    private String email;
    @Column(length = 1024)
    private String address;
    private String aadhaarNumber;
    private String panNumber;
    private boolean kycCompleted;
    // status: PENDING_APPROVAL, APPROVED, REJECTED
    private String status;

    // ===== GETTERS & SETTERS =====

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
 
    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }
 
    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public boolean isKycCompleted() {
        return kycCompleted;
    }
 
    public void setKycCompleted(boolean kycCompleted) {
        this.kycCompleted = kycCompleted;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // convenience getter for masked aadhaar used in templates
    public String getMaskedAadhaar() {
        if (this.aadhaarNumber == null) return "";
        String s = this.aadhaarNumber.replaceAll("\\s+", "");
        if (s.length() <= 4) return s;
        String last = s.substring(s.length() - 4);
        return "**** **** " + last;
    }
}
