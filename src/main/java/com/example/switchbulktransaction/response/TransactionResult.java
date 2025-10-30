package com.example.switchbulktransaction.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResult {
    private String transactionId;
    private String status;
    private String reason;
    
    public TransactionResult(String transactionId, String status) {
        this.transactionId = transactionId;
        this.status = status;
    }
}