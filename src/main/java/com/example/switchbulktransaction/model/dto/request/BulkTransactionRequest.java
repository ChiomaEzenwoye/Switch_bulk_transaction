package com.example.switchbulktransaction.model.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BulkTransactionRequest {
    
    @NotBlank(message = "Batch ID is required")
    private String batchId;

    @Valid
    @NotEmpty(message = "Transactions list cannot be empty")
    private List<TransactionRequest> transactions;

}