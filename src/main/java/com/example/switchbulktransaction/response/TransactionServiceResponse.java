package com.example.switchbulktransaction.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionServiceResponse {
    private String transactionId;
    private String status;
    private String message;
}