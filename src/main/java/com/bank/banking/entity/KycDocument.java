package com.bank.banking.entity;

import jakarta.persistence.*;

@Entity
public class KycDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long customerId;

    private String aadhaarFile;
    private String panFile;
    private String photoFile;
    private String addressProofFile;
    private String signatureFile;

    private String status; // PENDING / APPROVED / REJECTED

    // getters & setters

    public Long getId() {
        return id;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getAadhaarFile() {
        return aadhaarFile;
    }

    public void setAadhaarFile(String aadhaarFile) {
        this.aadhaarFile = aadhaarFile;
    }

    public String getPanFile() {
        return panFile;
    }

    public void setPanFile(String panFile) {
        this.panFile = panFile;
    }

    public String getPhotoFile() {
        return photoFile;
    }

    public void setPhotoFile(String photoFile) {
        this.photoFile = photoFile;
    }

    public String getAddressProofFile() {
        return addressProofFile;
    }

    public void setAddressProofFile(String addressProofFile) {
        this.addressProofFile = addressProofFile;
    }

    public String getSignatureFile() {
        return signatureFile;
    }

    public void setSignatureFile(String signatureFile) {
        this.signatureFile = signatureFile;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
