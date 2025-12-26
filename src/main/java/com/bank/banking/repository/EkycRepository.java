package com.bank.banking.repository;

import com.bank.banking.entity.Ekyc;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EkycRepository extends JpaRepository<Ekyc, Long> {
    List<Ekyc> findByStatus(String status);
}
