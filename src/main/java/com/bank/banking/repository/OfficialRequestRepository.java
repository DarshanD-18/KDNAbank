package com.bank.banking.repository;

import com.bank.banking.entity.OfficialRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

import java.util.List;

public interface OfficialRequestRepository extends JpaRepository<OfficialRequest, Long> {

    long countByStatus(String status);

    List<OfficialRequest> findByStatus(String status);

    Optional<OfficialRequest> findByUsername(String username);
}

