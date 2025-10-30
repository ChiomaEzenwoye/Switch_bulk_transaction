package com.example.switchbulktransaction.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
@Data
public class BulkRequestDto {
    @NotBlank(message = "Batch ID is required")
    private String batchId;

    @NotEmpty(message = "Transactions list cannot be empty")
    @Valid
    private List<TransactionDto> transactions;
}
