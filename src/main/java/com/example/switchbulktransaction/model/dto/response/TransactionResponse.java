package com.example.switchbulktransaction.model.dto.response;

import com.example.switchbulktransaction.enumeration.TransactionStatus;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {
    private String transactionId;
    private TransactionStatus status;
    private String reason;
}
