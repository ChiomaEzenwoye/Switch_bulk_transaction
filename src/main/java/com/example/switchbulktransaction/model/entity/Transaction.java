package com.example.switchbulktransaction.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String transactionId;
    private BigDecimal amount;
    private String fromAccount;
    private String toAccount;
    private int retry;
    private String reason;

}
