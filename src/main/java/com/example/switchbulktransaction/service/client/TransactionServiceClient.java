package com.example.switchbulktransaction.service.client;

import com.example.switchbulktransaction.model.dto.request.TransactionRequest;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;



import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionServiceClient {

    private final RestTemplate restTemplate;

    @Value("${transaction.service.url:localhost:8082}")
    private String transactionServiceUrl;

    @Value("${transaction.service.endpoint:/api/v1/transactions}")
    private String transactionServiceEndPoint;


    @Retry(name = "transactionRetry", fallbackMethod = "handleRetryFailure")
//    @CircuitBreaker(name = "transactionService", fallbackMethod = "handleCircuitBreaker")
    public ResponseEntity<String> sendTransaction(TransactionRequest request) {
        return restTemplate
                .postForEntity(transactionServiceUrl + transactionServiceEndPoint, request, String.class);
    }

    private ResponseEntity<String> handleRetryFailure(TransactionRequest request, Exception ex) {
        log.error("Transaction failed after retries: {} - {}", request.getTransactionId(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Retry failure: " + ex.getMessage());
    }

    private ResponseEntity<String> handleCircuitBreaker(TransactionRequest request, Exception ex) {
        log.error("Circuit breaker open - skipping call for: {} - {}", request.getTransactionId(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Circuit breaker open");
    }

}
