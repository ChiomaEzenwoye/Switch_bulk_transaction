package com.example.switchbulktransaction.model.entity;

import com.example.switchbulktransaction.enumeration.TransactionStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Builder
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String transactionId;
    private BigDecimal amount;
    private String fromAccount;
    private String toAccount;

    @Builder.Default
    private int retry = 0;

    private String reason;
    private String batchId;
    @Enumerated(EnumType.STRING)
    private TransactionStatus transactionStatus;

}
