package com.example.switchbulktransaction.client;

import com.example.switchbulktransaction.request.TransactionServiceRequest;
import com.example.switchbulktransaction.response.TransactionServiceResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionServiceClient {

    private final RestTemplate restTemplate;

    @Value("${transaction.service.url:http://localhost:8081}")
    private String transactionServiceUrl;

    @CircuitBreaker(name = "transactionService", fallbackMethod = "fallbackProcessTransaction")
    @Retry(name = "transactionService")
    public TransactionServiceResponse processTransaction(TransactionServiceRequest request) {
        String url = transactionServiceUrl + "/api/v1/transactions";

        log.debug("Calling Transaction-Service for transactionId: {}", request.getTransactionId());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<TransactionServiceRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<TransactionServiceResponse> response =
                restTemplate.postForEntity(url, entity, TransactionServiceResponse.class);

        return response.getBody();
    }

    private TransactionServiceResponse fallbackProcessTransaction(
            TransactionServiceRequest request, Exception e) {

        log.error("Fallback triggered for transaction: {}. Reason: {}",
                request.getTransactionId(), e.getMessage());

        return new TransactionServiceResponse(
                request.getTransactionId(),
                "FAILED",
                "Transaction-Service unavailable: " + e.getMessage()
        );
    }
}
