package com.example.switchbulktransaction.repository;

import com.example.switchbulktransaction.model.dto.response.TransactionMetrics;
import com.example.switchbulktransaction.model.entity.Transaction;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("SELECT new com.example.switchbulktransaction.model.dto.response.TransactionMetrics(t.transactionStatus, COUNT(t)) " +
            "FROM Transaction t GROUP BY t.transactionStatus")
    List<TransactionMetrics> getTransactionMetricsByTransactionStatus();
    List<Transaction> findByTransactionIdIn(List<String> transactionIds);

    boolean existsByTransactionId(@NotBlank(message = "Transaction ID is required") String transactionId);
}
