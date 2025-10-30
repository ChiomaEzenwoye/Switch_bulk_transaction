package com.example.switchbulktransaction.dto;

import lombok.Data;

@Data
public class TransactionResultDto {
    private String transactionId;
    private String status;
    private String message;
}
