package com.bank.banking.repository;

import com.bank.banking.entity.KycDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KycDocumentRepository extends JpaRepository<KycDocument, Long> {
    List<KycDocument> findByCustomerId(Long customerId);
    List<KycDocument> findByStatus(String status);
}
