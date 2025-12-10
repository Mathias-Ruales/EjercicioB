package com.example.demo;

// --- NEW IMPORTS (V4) ---
import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactBuilder; // Changed from PactDslWithProvider
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.V4Pact; // Changed from RequestResponsePact
import au.com.dius.pact.core.model.annotations.Pact;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "PaymentService")
class PaymentContractTest {

    // --- UPDATED METHOD FOR V4 ---
    @Pact(consumer = "OrderService", provider = "PaymentService")
    public V4Pact createPact(PactBuilder builder) { // Note the change to V4Pact and PactBuilder
        return builder
                .usingLegacyDsl() // Allows us to use the standard .given().uponReceiving()... syntax
                .given("payment service is ready")
                .uponReceiving("a request to authorize payment")
                .path("/payments/authorize")
                .method("POST")
                .headers("Content-Type", "application/json")
                .body("{\"card\": \"4111-2222-3333-4444\", \"amount\": 100}")
                .willRespondWith()
                .status(200)
                .body("{\"status\": \"APPROVED\", \"transactionId\": \"tx_999\"}")
                .toPact(V4Pact.class); // Explicitly build a V4 Pact
    }

    @Test
    void shouldAuthorizePayment(MockServer mockServer) {
        // This part stays exactly the same!
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String jsonBody = "{\"card\": \"4111-2222-3333-4444\", \"amount\": 100}";
        HttpEntity<String> request = new HttpEntity<>(jsonBody, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                mockServer.getUrl() + "/payments/authorize",
                request,
                String.class
        );

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("{\"status\": \"APPROVED\", \"transactionId\": \"tx_999\"}", response.getBody());
    }
}