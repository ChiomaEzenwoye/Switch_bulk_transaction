package com.example.switchbulktransaction.controller;

import com.example.switchbulktransaction.dto.request.BulkTransactionRequest;
import com.example.switchbulktransaction.dto.response.BulkTransactionResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/")
public class BulkTransactionController {



    @PreAuthorize("hasRole('USER')")
    @PostMapping("bulk-transactions")
    public BulkTransactionResponse processBulkTransactions(@RequestBody BulkTransactionRequest request){
        return new BulkTransactionResponse();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("bulk-transactions-admin")
    public BulkTransactionResponse testAdmin(@RequestBody BulkTransactionRequest request){
        return new BulkTransactionResponse();
    }



}
