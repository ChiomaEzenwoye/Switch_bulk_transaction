package com.example.switchbulktransaction.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
public class LoggerUtils {


    public static void logTransaction(String transactionId, String batchId){
        LocalDateTime processingTime = LocalDateTime.now();
        log.debug("[ {} ] - Transaction processing for ID: [ {}  ] with Batch ID: [ {} ]", processingTime, transactionId, batchId);
    }
}
