package com.banking.repository;

import com.banking.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer,Long> {

    Optional<Customer> findByEmail(String email);
    Optional<Customer> findByPanCard(String panCard);
    Optional<Customer> findByAadharCard(String aadharCard);
}
