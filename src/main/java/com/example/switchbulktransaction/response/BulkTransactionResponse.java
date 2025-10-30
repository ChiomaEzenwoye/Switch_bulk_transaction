package com.example.switchbulktransaction.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BulkTransactionResponse {
    private String batchId;
    private List<TransactionResult> results;
}
