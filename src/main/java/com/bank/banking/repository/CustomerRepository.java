package com.bank.banking.repository;

import com.bank.banking.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByMobileNumber(String mobileNumber);

    List<Customer> findByStatus(String status);

    List<Customer> findByStatusOrderByIdDesc(String status);
}
