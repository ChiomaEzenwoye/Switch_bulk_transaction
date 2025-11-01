package com.example.switchbulktransaction.service;

import com.example.switchbulktransaction.enumeration.TransactionStatus;
import com.example.switchbulktransaction.model.dto.request.BulkTransactionRequest;
import com.example.switchbulktransaction.model.dto.request.TransactionRequest;
import com.example.switchbulktransaction.model.dto.response.BulkTransactionResponse;
import com.example.switchbulktransaction.model.dto.response.TransactionMetrics;
import com.example.switchbulktransaction.model.dto.response.TransactionResponse;
import com.example.switchbulktransaction.model.entity.Transaction;
import com.example.switchbulktransaction.repository.TransactionRepository;
import com.example.switchbulktransaction.service.client.TransactionServiceClient;
import com.example.switchbulktransaction.util.LoggerUtils;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class BulkProcessingServiceImplementation implements BulkProcessingService {

    private final TransactionServiceClient transactionServiceClient;
    private final TransactionRepository transactionRepository;

    @Transactional
    public BulkTransactionResponse processBulkTransactions(BulkTransactionRequest request) {

        BulkTransactionResponse response = new BulkTransactionResponse();
        response.setBatchId(request.getBatchId());

        List<TransactionResponse> results = Collections.synchronizedList(new ArrayList<>());

        List<CompletableFuture<Void>> futures = request.getTransactions().stream()
                .map(txRequest -> CompletableFuture.runAsync(() -> {
                    LoggerUtils.logTransaction(txRequest.getTransactionId(), request.getBatchId());
                    try {
                        processSuccessFullTransaction(request, txRequest);
                        results.add(new TransactionResponse(txRequest.getTransactionId(), TransactionStatus.SUCCESS, null));
                    } catch (Exception e) {
                        processFailedTransactions(request, txRequest, e);
                        results.add(new TransactionResponse(txRequest.getTransactionId(), TransactionStatus.FAILED, e.getMessage()));
                    }
                }))
                .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        response.setResults(results);
        return response;
    }

    private void processFailedTransactions(BulkTransactionRequest request, TransactionRequest txRequest, Exception e) {
        Transaction transaction = new Transaction();
        transaction.setBatchId(request.getBatchId());
        transaction.setTransactionId(txRequest.getTransactionId());
        transaction.setTransactionStatus(TransactionStatus.FAILED);
        transaction.setReason("Unable to complete Transaction");
        transactionRepository.save(transaction);
    }

    private void processSuccessFullTransaction(BulkTransactionRequest request, TransactionRequest txRequest) {
        Transaction transaction = new Transaction();
        transaction.setBatchId(request.getBatchId());
        transaction.setTransactionId(txRequest.getTransactionId());
        transaction.setFromAccount(txRequest.getFromAccount());
        transaction.setToAccount(txRequest.getToAccount());
        transaction.setAmount(txRequest.getAmount());

        ResponseEntity<String> responseEntity = transactionServiceClient
                .sendTransaction(txRequest);
        if (responseEntity.getStatusCode().is2xxSuccessful()){
            transaction.setTransactionStatus(TransactionStatus.SUCCESS);
        }
        transactionRepository.save(transaction);
    }



    public List<TransactionMetrics> getTransactionMetricsByStatus(){
        return transactionRepository.getTransactionMetricsByTransactionStatus();
    }
}
