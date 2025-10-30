package com.example.switchbulktransaction.dto;

import com.example.switchbulktransaction.response.TransactionResult;
import lombok.Data;

import java.util.List;
@Data
public class BulkResponsedto {
    private String batchId;
    private List<TransactionResultDto> results;
}
