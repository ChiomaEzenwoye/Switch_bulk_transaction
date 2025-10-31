package com.example.switchbulktransaction.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class BulkTransactionResponse {
    private String batchId;
    private List<TransactionResponse> results;
}
