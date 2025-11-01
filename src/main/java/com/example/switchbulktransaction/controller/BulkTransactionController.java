package com.example.switchbulktransaction.controller;

import com.example.switchbulktransaction.model.dto.request.BulkTransactionRequest;
import com.example.switchbulktransaction.model.dto.response.ApiResponse;
import com.example.switchbulktransaction.model.dto.response.BulkTransactionResponse;
import com.example.switchbulktransaction.model.dto.response.TransactionMetrics;
import com.example.switchbulktransaction.service.BulkProcessingService;
import com.example.switchbulktransaction.service.BulkProcessingServiceImplementation;
import com.example.switchbulktransaction.util.Roles;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class BulkTransactionController {

    private final BulkProcessingService bulkProcessingService;

    @PreAuthorize(Roles.HAS_USER_ROLE)
    @PostMapping("bulk-transactions")
    public ResponseEntity<ApiResponse<BulkTransactionResponse>> processBulkTransactions(@Valid @RequestBody BulkTransactionRequest request){
        return new ResponseEntity<>(new ApiResponse<>("processed", bulkProcessingService.processBulkTransactions(request)), HttpStatus.OK);
    }

    @PreAuthorize(Roles.HAS_ADMIN_ROLE)
    @PostMapping("transactions-metrics")
    public ResponseEntity<ApiResponse<List<TransactionMetrics>>> getTransactionMetricsByStatus(){
       return  new ResponseEntity<>(new ApiResponse<>("processed", bulkProcessingService.getTransactionMetricsByStatus()), HttpStatus.OK);
    }



}
