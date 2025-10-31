package com.example.switchbulktransaction.controller;

import com.example.switchbulktransaction.model.dto.request.BulkTransactionRequest;
import com.example.switchbulktransaction.model.dto.response.BulkTransactionResponse;
import com.example.switchbulktransaction.util.Roles;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/api/v1/")
public class BulkTransactionController {


    @PreAuthorize(Roles.HAS_USER_ROLE)
    @PostMapping("bulk-transactions")
    public BulkTransactionResponse processBulkTransactions(@Valid @RequestBody BulkTransactionRequest request){
        return new BulkTransactionResponse();
    }

    @PreAuthorize(Roles.HAS_ADMIN_ROLE)
    @PostMapping("bulk-transactions-admin")
    public BulkTransactionResponse testAdmin(@Valid @RequestBody BulkTransactionRequest request){
        return new BulkTransactionResponse();
    }



}
