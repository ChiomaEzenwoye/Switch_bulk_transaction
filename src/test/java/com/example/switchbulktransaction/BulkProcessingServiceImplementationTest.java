package com.example.switchbulktransaction;

import com.example.switchbulktransaction.enumeration.TransactionStatus;
import com.example.switchbulktransaction.model.dto.request.BulkTransactionRequest;
import com.example.switchbulktransaction.model.dto.request.TransactionRequest;
import com.example.switchbulktransaction.model.dto.response.BulkTransactionResponse;
import com.example.switchbulktransaction.model.entity.Transaction;
import com.example.switchbulktransaction.repository.TransactionRepository;
import com.example.switchbulktransaction.service.BulkProcessingServiceImplementation;
import com.example.switchbulktransaction.service.client.TransactionServiceClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class BulkProcessingServiceImplementationTest {

    @Mock
    private TransactionServiceClient transactionServiceClient;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private BulkProcessingServiceImplementation bulkProcessingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private BulkTransactionRequest createSampleRequest() {
        TransactionRequest t1 = new TransactionRequest("tx001", "111", "222", BigDecimal.valueOf(100.00));
        TransactionRequest t2 = new TransactionRequest("tx002", "111", "333", BigDecimal.valueOf(2000.0));

        BulkTransactionRequest req = new BulkTransactionRequest();
        req.setBatchId("BATCH-001");
        req.setTransactions(List.of(t1, t2));
        return req;
    }

    // ✅ Test Case: Handle  successful scenario
// We mock this by making the  call success
    @Test
    void shouldProcessAllTransactionsSuccessfully() {
        // Mock client to always succeed
        when(transactionServiceClient.sendTransaction(any(TransactionRequest.class)))
                .thenReturn(ResponseEntity.ok("Success"));

        BulkTransactionResponse response = bulkProcessingService.processBulkTransactions(createSampleRequest());

        assertThat(response.getResults()).hasSize(2);
        assertThat(response.getResults()).allMatch(r -> r.getStatus() == TransactionStatus.SUCCESS);
        verify(transactionRepository, times(2)).save(any(Transaction.class));
     }

    // ✅ Test Case: Handle partial failure
   // This test makes sure that if one transaction goes through and another one fails,
   // the system records one as SUCCESS and the other as FAILED.
   // We fake this by making the first call succeed and the second one throw an error.
    @Test
    void shouldHandlePartialFailure() {
        // One success, one failure
        when(transactionServiceClient.sendTransaction(any(TransactionRequest.class)))
                .thenReturn(ResponseEntity.ok("Success"))
                .thenThrow(new RuntimeException("Simulated failure"));

        BulkTransactionResponse response = bulkProcessingService.processBulkTransactions(createSampleRequest());

        long successCount = response.getResults().stream()
                .filter(r -> r.getStatus() == TransactionStatus.SUCCESS).count();
        long failCount = response.getResults().stream()
                .filter(r -> r.getStatus() == TransactionStatus.FAILED).count();

        assertThat(successCount).isEqualTo(1);
        assertThat(failCount).isEqualTo(1);
    }

    // ✅ Test Case: Handle  failed scenario
    // We mock this by making the runtimeException success

    @Test
    void shouldHandleCompleteFailure() {
        when(transactionServiceClient.sendTransaction(any(TransactionRequest.class)))
                .thenThrow(new RuntimeException("Client down"));

        BulkTransactionResponse response = bulkProcessingService.processBulkTransactions(createSampleRequest());

        assertThat(response.getResults()).allMatch(r -> r.getStatus() == TransactionStatus.FAILED);
    }
}
