package com.example.switchbulktransaction.service;

import com.example.switchbulktransaction.model.dto.request.BulkTransactionRequest;
import com.example.switchbulktransaction.model.dto.response.BulkTransactionResponse;
import com.example.switchbulktransaction.model.dto.response.TransactionMetrics;

import java.util.List;

public interface BulkProcessingService {

    BulkTransactionResponse processBulkTransactions(BulkTransactionRequest request);
    List<TransactionMetrics> getTransactionMetricsByStatus();
}
