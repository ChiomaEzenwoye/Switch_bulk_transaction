package com.example.switchbulktransaction.model.dto.response;

import com.example.switchbulktransaction.enumeration.TransactionStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TransactionMetrics {
    private TransactionStatus transactionStatus;
    private Long count;
}
