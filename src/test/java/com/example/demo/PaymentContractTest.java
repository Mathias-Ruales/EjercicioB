package com.example.demo;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "PaymentService")
class PaymentContractTest {

    // --- STEP 1: DEFINE THE CONTRACT ---
    // "I expect the PaymentService to return 200 OK when I send a valid card."
    @Pact(consumer = "OrderService", provider = "PaymentService")
    public RequestResponsePact createPact(PactDslWithProvider builder) {
        return builder
                .given("payment service is ready")
                .uponReceiving("a request to authorize payment")
                .path("/payments/authorize")
                .method("POST")
                .headers("Content-Type", "application/json")
                .body("{\"card\": \"4111-1111\", \"amount\": 100}")
                .willRespondWith()
                .status(200)
                .body("{\"status\": \"APPROVED\", \"id\": \"tx_123\"}")
                .toPact();
    }

    // --- STEP 2: VERIFY THE CONTRACT ---
    // We run this test to ensure OUR code (OrderService) can actually talk to that contract.
    @Test
    void shouldAuthorizePayment(MockServer mockServer) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // We send exactly what we promised in the Pact above
        String jsonBody = "{\"card\": \"4111-1111\", \"amount\": 100}";
        HttpEntity<String> request = new HttpEntity<>(jsonBody, headers);

        // We call the Mock Server (which represents the Provider)
        ResponseEntity<String> response = restTemplate.postForEntity(
                mockServer.getUrl() + "/payments/authorize",
                request,
                String.class
        );

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("{\"status\": \"APPROVED\", \"id\": \"tx_123\"}", response.getBody());
    }
}