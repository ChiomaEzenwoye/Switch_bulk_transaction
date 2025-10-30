package com.example.switchbulktransaction.service;

import com.example.switchbulktransaction.client.TransactionServiceClient;
import com.example.switchbulktransaction.dto.TransactionDto;
import com.example.switchbulktransaction.request.BulkTransactionRequest;
import com.example.switchbulktransaction.request.TransactionServiceRequest;
import com.example.switchbulktransaction.response.BulkTransactionResponse;
import com.example.switchbulktransaction.response.TransactionResult;
import com.example.switchbulktransaction.response.TransactionServiceResponse;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import io.micrometer.core.instrument.Counter;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class BulkTransactionService {
    
    private final TransactionServiceClient transactionServiceClient;
    private final Counter successCounter;
    private final Counter failureCounter;
    
    public BulkTransactionService(TransactionServiceClient transactionServiceClient, 
                                  MeterRegistry meterRegistry) {
        this.transactionServiceClient = transactionServiceClient;
        this.successCounter = Counter.builder("transactions.success")
                .description("Number of successful transactions")
                .register(meterRegistry);
        this.failureCounter = Counter.builder("transactions.failure")
                .description("Number of failed transactions")
                .register(meterRegistry);
    }
    
    public BulkTransactionResponse processBulkTransactions(BulkTransactionRequest request) {
        String batchId = request.getBatchId();
        log.info("Processing batch: {} with {} transactions", batchId, request.getTransactions().size());
        
        // Validate unique transaction IDs
        validateUniqueTransactionIds(request.getTransactions());
        
        List<TransactionResult> results = new ArrayList<>();
        
        for (TransactionDto transaction : request.getTransactions()) {
            String transactionId = transaction.getTransactionId();
            
            try {
                log.debug("Processing transaction: {} in batch: {}", transactionId, batchId);
                
                TransactionServiceRequest serviceRequest = new TransactionServiceRequest(
                        transaction.getTransactionId(),
                        transaction.getFromAccount(),
                        transaction.getToAccount(),
                        transaction.getAmount()
                );
                
                TransactionServiceResponse serviceResponse =
                        transactionServiceClient.processTransaction(serviceRequest);
                
                if ("SUCCESS".equalsIgnoreCase(serviceResponse.getStatus())) {
                    results.add(new TransactionResult(transactionId, "SUCCESS"));
                    successCounter.increment();
                    log.info("Transaction {} processed successfully in batch: {}", transactionId, batchId);
                } else {
                    String reason = serviceResponse.getMessage() != null ? 
                            serviceResponse.getMessage() : "Transaction failed";
                    results.add(new TransactionResult(transactionId, "FAILED", reason));
                    failureCounter.increment();
                    log.warn("Transaction {} failed in batch: {}. Reason: {}", 
                            transactionId, batchId, reason);
                }
                
            } catch (Exception e) {
                log.error("Error processing transaction: {} in batch: {}", transactionId, batchId, e);
                results.add(new TransactionResult(transactionId, "FAILED", e.getMessage()));
                failureCounter.increment();
            }
        }
        
        log.info("Batch {} processing complete. Success: {}, Failed: {}", 
                batchId, 
                results.stream().filter(r -> "SUCCESS".equals(r.getStatus())).count(),
                results.stream().filter(r -> "FAILED".equals(r.getStatus())).count());
        
        return new BulkTransactionResponse(batchId, results);
    }
    
    private void validateUniqueTransactionIds(List<TransactionDto> transactions) {
        Set<String> transactionIds = new HashSet<>();
        for (TransactionDto transaction : transactions) {
            if (!transactionIds.add(transaction.getTransactionId())) {
                throw new IllegalArgumentException(
                        "Duplicate transaction ID found: " + transaction.getTransactionId());
            }
        }
    }
}