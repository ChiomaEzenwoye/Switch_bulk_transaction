package com.example.switchbulktransaction.service;


import com.example.switchbulktransaction.model.dto.request.BulkTransactionRequest;
import com.example.switchbulktransaction.model.dto.request.TransactionRequest;
import com.example.switchbulktransaction.model.dto.response.ApiResponse;
import com.example.switchbulktransaction.service.client.TransactionServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class BulkProcessingService {

    private final TransactionServiceClient transactionServiceClient;

    public ApiResponse<List<String>> processBulkTransactions(BulkTransactionRequest bulkRequest) {
        List<String> responses = new ArrayList<>();

        log.info("Starting bulk transaction processing for batch ID: {}", bulkRequest.getBatchId());

        for (TransactionRequest request : bulkRequest.getTransactions()) {
            try {
                log.info("Processing transaction ID: {}", request.getTransactionId());
                ResponseEntity<String> response = transactionServiceClient.sendTransaction(request);
                responses.add("Transaction ID: " + request.getTransactionId() + " - Response: " + response.getBody());
            } catch (Exception ex) {
                log.error("Error processing transaction ID: {}", request.getTransactionId(), ex);
                responses.add("Transaction ID: " + request.getTransactionId() + " - Failed: " + ex.getMessage());
            }
        }

        ApiResponse<List<String>> apiResponse = new ApiResponse<>();
        apiResponse.setMessage("Batch " + bulkRequest.getBatchId() + " processed successfully");
        apiResponse.setData(responses);
        return apiResponse;
    }
}


