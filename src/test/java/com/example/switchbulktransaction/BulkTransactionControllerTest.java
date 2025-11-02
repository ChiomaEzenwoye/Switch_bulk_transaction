package com.example.switchbulktransaction;

import com.example.switchbulktransaction.config.security.SecurityConfig;
import com.example.switchbulktransaction.model.dto.request.BulkTransactionRequest;
import com.example.switchbulktransaction.model.dto.request.TransactionRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
@Transactional
class BulkTransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    /**
     * Test Case: Unauthorized Access (401)
     * <p>
     * This test verifies that when a request is made to the bulk transaction endpoint
     * **without providing any JWT token in the Authorization header**,
     * the system correctly denies access and responds with HTTP 401 Unauthorized.
     * <p>
     * Including `.with(csrf())` ensures the request passes CSRF validation,
     * allowing us to isolate and confirm that the rejection is due to **missing authentication**
     * — not a CSRF issue.
     */
    @Test
    void shouldReturnUnauthorizedWithoutToken() throws Exception {
        mockMvc.perform(post("/api/v1/bulk-transactions")
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }


    // ✅ Process bulk transactions with a valid user token
    // This test makes sure that when a valid JWT with the USER role is provided,
    // the bulk transaction request is processed successfully (status 200 OK)
    // and the correct batch ID is returned in the response.
    @Test
    void shouldProcessSuccessfullyWithValidToken() throws Exception {
        BulkTransactionRequest req = new BulkTransactionRequest();
        req.setBatchId("BATCH-TEST");
        req.setTransactions(List.of(
                new TransactionRequest("t1", "123", "456", BigDecimal.valueOf(100.0)),
                new TransactionRequest("t2", "789", "012", BigDecimal.valueOf(50.0))
        ));

        mockMvc.perform(post("/api/v1/bulk-transactions")
                        .with(jwt().authorities(() -> "ROLE_USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.batchId").value("BATCH-TEST"));
    }

}

