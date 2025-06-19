package ua.edu.ukma.clientserverjava;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MySQLContainer;

class MysqlDatabaseTest {

    private static final MySQLContainer<?> MYSQL = new MySQLContainer<>();

    private Database database;

    @BeforeAll
    static void startDb() {
        MYSQL.start();
    }

    @AfterAll
    static void stopDb() {
        MYSQL.stop();
    }

    @BeforeEach
    void setup() {
        database = new MysqlDatabase(MYSQL.getHost() + ":" + MYSQL.getMappedPort(3306), MYSQL.getDatabaseName(), MYSQL.getUsername(), MYSQL.getPassword());

        // todo: clean database before or after test
    }

    @Test
    void shouldInsertData() {
        Assertions.assertThat(database.getAllData())
            .isEmpty();

        TestRow inserted = database.insertTestData(new TestRow(null, "row1"));
        Assertions.assertThat(inserted.id())
            .isNotNull();

        Assertions.assertThat(database.getAllData())
            .hasSize(1);
    }

}