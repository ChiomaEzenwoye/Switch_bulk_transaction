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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

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

        //  Validate transactions (check for duplicates)
        List<TransactionRequest> duplicates = findDuplicateTransactions(request.getTransactions());
        List<TransactionRequest> newTransactions = request.getTransactions().stream()
                .filter(tx -> duplicates.stream()
                        .noneMatch(dup -> dup.getTransactionId().equals(tx.getTransactionId())))
                .toList();

        // Add duplicates to the result
        duplicates.forEach(dup ->
                results.add(new TransactionResponse(
                        dup.getTransactionId(),
                        TransactionStatus.FAILED,
                        "Duplicate transaction found"))
        );

        //Process only new transactions
        List<CompletableFuture<Void>> futures = newTransactions.stream()
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

    /**
     * Finds duplicate transactions based on transactionId already existing in DB.
     */
    private List<TransactionRequest> findDuplicateTransactions(List<TransactionRequest> transactions) {
        List<String> transactionIds = transactions.stream()
                .map(TransactionRequest::getTransactionId)
                .toList();

        List<String> existingIds = transactionRepository.findByTransactionIdIn(transactionIds)
                .stream()
                .map(Transaction::getTransactionId)
                .toList();

        return transactions.stream()
                .filter(tx -> existingIds.contains(tx.getTransactionId()))
                .collect(Collectors.toList());
    }

    private void processFailedTransactions(BulkTransactionRequest request, TransactionRequest txRequest, Exception e) {
        Transaction transaction = new Transaction();
        transaction.setBatchId(request.getBatchId());
        transaction.setTransactionId(txRequest.getTransactionId());
        transaction.setTransactionStatus(TransactionStatus.FAILED);
        transaction.setReason("Unable to complete Transaction: " + e.getMessage());
        transactionRepository.save(transaction);
    }

    private void processSuccessFullTransaction(BulkTransactionRequest request, TransactionRequest txRequest) {
        Transaction transaction = new Transaction();
        transaction.setBatchId(request.getBatchId());
        transaction.setTransactionId(txRequest.getTransactionId());
        transaction.setFromAccount(txRequest.getFromAccount());
        transaction.setToAccount(txRequest.getToAccount());
        transaction.setAmount(txRequest.getAmount());

        ResponseEntity<String> responseEntity = transactionServiceClient.sendTransaction(txRequest);
        transaction.setTransactionStatus(
                responseEntity.getStatusCode().is2xxSuccessful()
                        ? TransactionStatus.SUCCESS
                        : TransactionStatus.FAILED
        );

        transactionRepository.save(transaction);
    }

    public List<TransactionMetrics> getTransactionMetricsByStatus() {
        return transactionRepository.getTransactionMetricsByTransactionStatus();
    }
}
