package com.example.demo;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime date;
    private Double amount;

    // Default constructor required by JPA
    public Transaction() {}

    public Transaction(LocalDateTime date, Double amount) {
        this.date = date;
        this.amount = amount;
    }

    public Long getId() { return id; }
    public LocalDateTime getDate() { return date; }
    public Double getAmount() { return amount; }
}