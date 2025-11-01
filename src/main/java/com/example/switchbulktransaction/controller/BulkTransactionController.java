package com.example.switchbulktransaction.controller;

import com.example.switchbulktransaction.model.dto.request.BulkTransactionRequest;
import com.example.switchbulktransaction.model.dto.response.ApiResponse;
import com.example.switchbulktransaction.service.BulkProcessingService;
import com.example.switchbulktransaction.util.Roles;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class BulkTransactionController {

    private final BulkProcessingService bulkTransactionService;

    @PreAuthorize(Roles.HAS_USER_ROLE)
    @PostMapping("bulk-transactions")
    public ApiResponse<?> processBulkTransactions(@Valid @RequestBody BulkTransactionRequest request) {
        return bulkTransactionService.processBulkTransactions(request);
    }

    @PreAuthorize(Roles.HAS_ADMIN_ROLE)
    @PostMapping("bulk-transactions-admin")
    public ApiResponse<?> testAdmin(@Valid @RequestBody BulkTransactionRequest request) {
        return bulkTransactionService.processBulkTransactions(request);
    }
}
