package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
// Crucial: Tells Spring NOT to replace our Docker DB with H2
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TransactionRepositoryTest {

    // Define the PostgreSQL Docker container
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    // Overwrite application.properties to connect to the random Docker port
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    TransactionRepository repository;

    @Test
    void shouldFindTransactionsWithinDateRange() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        repository.save(new Transaction(now.minusDays(5), 100.0)); // Too old
        repository.save(new Transaction(now, 200.0));              // Target
        repository.save(new Transaction(now.plusDays(2), 300.0));  // Future

        // Act
        List<Transaction> result = repository.findByDateBetween(
                now.minusHours(1),
                now.plusHours(1)
        );

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getAmount()).isEqualTo(200.0);
    }
}