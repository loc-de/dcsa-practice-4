package com.polis;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.List;

class DatabaseTest {

    private static final PostgreSQLContainer<?> container = new PostgreSQLContainer<>();
    private Database db;

    @BeforeAll
    static void startDb() {
        container.start();
    }

    @AfterAll
    static void stopDb() {
        container.stop();
    }

    @BeforeEach
    void setUp() {
        db = new Database(
                "org.postgresql.Driver",
                container.getJdbcUrl(),
                container.getUsername(),
                container.getPassword()
        );
    }

    @AfterEach
    void tearDown() {
        db.clearTable();
    }

    @Test
    void shouldInsert() {
        Product inserted = db.insert(new Product(null, "item1", "ca1", 5, 899.99));
        Assertions.assertThat(inserted.id()).isNotNull();

        List<Product> products = db.getAll();
        Assertions.assertThat(products).hasSize(1);
        Assertions.assertThat(products.getFirst().name()).isEqualTo("item1");
    }

    @Test
    void shouldUpdate() {
        Product inserted = db.insert(new Product(null, "item2", "ca2", 10, 199.99));

        Product updated = new Product(inserted.id(), "item3", "ca2", 8, 249.99);
        db.update(updated);

        List<Product> products = db.getAll();
        Assertions.assertThat(products).hasSize(1);
        Assertions.assertThat(products.getFirst().name()).isEqualTo("item3");
    }

    @Test
    void shouldDelete() {
        Product inserted = db.insert(new Product(null, "item4", "ca2", 15, 49.99));
        db.delete(inserted.id());

        List<Product> products = db.getAll();
        Assertions.assertThat(products).isEmpty();
    }

    @Test
    void shouldFindByName() {
        db.insert(new Product(null, "item5", "ca1", 3, 150.0));
        db.insert(new Product(null, "item6", "ca1", 10, 20.0));
        db.insert(new Product(null, "item7", "ca1", 5, 45.0));

        ProductSearchParams params = new ProductSearchParams();
        params.setName("item");

        List<Product> result = db.searchProducts(params);
        Assertions.assertThat(result).hasSize(3);
    }

    @Test
    void shouldFilterByPrice() {
        db.insert(new Product(null, "item8", "ca3", 1, 10.0));
        db.insert(new Product(null, "item9", "ca3", 1, 50.0));
        db.insert(new Product(null, "item10", "ca3", 1, 100.0));

        ProductSearchParams params = new ProductSearchParams();
        params.setPriceMin(20.0);
        params.setPriceMax(80.0);

        List<Product> result = db.searchProducts(params);
        Assertions.assertThat(result).hasSize(1);
        Assertions.assertThat(result.getFirst().name()).isEqualTo("item9");
    }

    @Test
    void shouldPaginate() {
        for (int i = 1; i <= 5; i++) {
            db.insert(new Product(null, "item" + i, "ca4", 1, 10.0 * i));
        }

        ProductSearchParams params = new ProductSearchParams();
        params.setPage(1);
        params.setPageSize(2);

        List<Product> page = db.searchProducts(params);
        Assertions.assertThat(page).hasSize(2);
        Assertions.assertThat(page.getFirst().name()).isEqualTo("item3");
    }
}