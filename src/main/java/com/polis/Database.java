package ua.edu.ukma.clientserverjava;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public abstract class Database {

    private final Connection connection;

    public Database(String driverClass, String connectionString, String username, String password) {
        System.out.println("Connecting to database: " + connectionString);

        try {
            Class.forName(driverClass);
            connection = DriverManager.getConnection(connectionString, username, password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Can't find driver class", e);
        }

        createTable();
    }

    private void createTable() {
        // sql lite
//        try (PreparedStatement st = connection.prepareStatement("create table if not exists test (id INTEGER PRIMARY KEY AUTOINCREMENT, name varchar(1000));")) {
        // mysql
        try (PreparedStatement st = connection.prepareStatement("create table if not exists test (id INTEGER PRIMARY KEY AUTO_INCREMENT, name varchar(1000));")) {
            st.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public TestRow insertTestData(TestRow row) {
        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO test(name) VALUES (?)", Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, row.name());

            // execute insert
            statement.executeUpdate();

            // get newly generated id
            ResultSet rs = statement.getGeneratedKeys();
            rs.next();
            int newId = rs.getInt(1);

            return new TestRow(newId, row.name());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<TestRow> getAllData() {
        try (Statement statement = connection.createStatement()) {
            List<TestRow> result = new ArrayList<>();
            ResultSet res = statement.executeQuery("SELECT * FROM test");

            while (res.next()) {
                int id = res.getInt("id");
                String name = res.getString("name");
//                int id = res.getInt(1);
//                String name = res.getString(2);
                result.add(new TestRow(id, name));
            }

            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
