package com.example.switchbulktransaction.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
public class TransactionDto {
    private String transactionId;
    private String fromAccount;
    private String toAccount;
    private BigDecimal amount;
}