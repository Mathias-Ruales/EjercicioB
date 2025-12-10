package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // Custom query to find transactions between two dates
    List<Transaction> findByDateBetween(LocalDateTime start, LocalDateTime end);
}