package com.example.switchbulktransaction.mapper;

import com.example.switchbulktransaction.model.dto.response.TransactionResponse;
import com.example.switchbulktransaction.enumeration.TransactionStatus;


public class MapperUtil {


    public static TransactionResponse mapToTransactionResponse(String transactionId, TransactionStatus status, String message){
         TransactionResponse transactionResponse = new TransactionResponse();
         transactionResponse.setTransactionId(transactionId);
         transactionResponse.setStatus(status);
         transactionResponse.setReason(message);
        return transactionResponse;
    }



}
