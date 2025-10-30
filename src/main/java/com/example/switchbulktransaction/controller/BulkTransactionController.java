package com.example.switchbulktransaction.controller;

import com.example.switchbulktransaction.request.BulkTransactionRequest;
import com.example.switchbulktransaction.response.BulkTransactionResponse;
import com.example.switchbulktransaction.service.BulkTransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/bulk-transactions")
@RequiredArgsConstructor
public class BulkTransactionController {
    
    private final BulkTransactionService bulkTransactionService;
    
    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<BulkTransactionResponse> processBulkTransactions(
            @Valid @RequestBody BulkTransactionRequest request) {
        
        log.info("Received bulk transaction request for batchId: {}", request.getBatchId());
        
        BulkTransactionResponse response = bulkTransactionService.processBulkTransactions(request);
        
        log.info("Completed bulk transaction processing for batchId: {}", request.getBatchId());
        
        return ResponseEntity.ok(response);
    }
}