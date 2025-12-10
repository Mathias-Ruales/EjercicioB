package com.example.demo;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@WireMockTest(httpPort = 8081) // Spinds up a "Fake Internet" on port 8081
class ExternalBankTest {

    @Test
    void shouldCallExternalBankApi() {
        // 1. TEACH WIREMOCK: "If you see a GET request to /currency-rates, return this JSON"
        WireMock.stubFor(WireMock.get(WireMock.urlEqualTo("/currency-rates"))
                .willReturn(WireMock.aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"USD\": 1.0, \"EUR\": 0.92}")
                        .withStatus(200)));

        // 2. TEST YOUR CODE: Imagine this is your service calling the bank
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject("http://localhost:8081/currency-rates", String.class);

        // 3. VERIFY: Did we get the data?
        Assertions.assertEquals("{\"USD\": 1.0, \"EUR\": 0.92}", response);

        // 4. VERIFY: Did we actually make the call?
        WireMock.verify(WireMock.getRequestedFor(WireMock.urlEqualTo("/currency-rates")));
    }
}