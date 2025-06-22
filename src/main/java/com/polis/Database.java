package com.polis;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {

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
        try (PreparedStatement st = connection.prepareStatement("""
            create table if not exists products (
                id serial primary key, 
                name varchar(100),
                category varchar(50),
                amount int,
                price numeric
            );"""
        )) {
            st.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void clearTable() {
        try (Statement st = connection.createStatement()) {
            st.execute("delete from products");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Product insert(Product row) {
        try (PreparedStatement st = connection.prepareStatement("""
            insert into products(name, category, amount, price) values (?, ?, ?, ?)
            """, Statement.RETURN_GENERATED_KEYS
        )) {
            st.setString(1, row.name());
            st.setString(2, row.category());
            st.setInt(3, row.amount());
            st.setDouble(4, row.price());
            st.executeUpdate();

            ResultSet rs = st.getGeneratedKeys();
            rs.next();
            int newId = rs.getInt(1);

            return new Product(newId, row.name(), row.category(), row.amount(), row.price());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(int id) {
        try (PreparedStatement st = connection.prepareStatement("""
            delete from products where id = ?
        """)) {
            st.setInt(1, id);
            st.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void update(Product row) {
        try (PreparedStatement st = connection.prepareStatement("""
            update products set name = ?, category = ?, amount = ?, price = ? where id = ?
        """)) {
            st.setString(1, row.name());
            st.setString(2, row.category());
            st.setInt(3, row.amount());
            st.setDouble(4, row.price());
            st.setInt(5, row.id());
            st.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Product> getAll() {
        try (Statement st = connection.createStatement()) {
            List<Product> result = new ArrayList<>();
            ResultSet res = st.executeQuery("select * from products");

            while (res.next()) {
                int id = res.getInt("id");
                String name = res.getString("name");
                String category = res.getString("category");
                int amount = res.getInt("amount");
                double price = res.getDouble("price");

                result.add(new Product(id, name, category, amount, price));
            }

            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Product> searchProducts(ProductSearchParams params) {
        List<Product> result = new ArrayList<>();

        StringBuilder builder = new StringBuilder("select * from products");

        builder.append(appendFilter(params));
        builder.append(appendOrderByClause(params));
        builder.append(appendPaginationClause(params));

        try (Statement st = connection.createStatement()) {
            ResultSet rs = st.executeQuery(builder.toString());

            while (rs.next()) {
                Product product = new Product(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("category"),
                        rs.getInt("amount"),
                        rs.getDouble("price")
                );
                result.add(product);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    private String appendFilter(ProductSearchParams params) {
        List<String> filters = new ArrayList<>();

        filters.add(buildLike("name", params.getName()));
        filters.add(buildLike("category", params.getCategory()));

        filters.add(buildMin("price", params.getPriceMin()));
        filters.add(buildMax("price", params.getPriceMax()));

        filters.add(buildMin("amount", params.getAmountMin()));
        filters.add(buildMax("amount", params.getAmountMax()));

        filters.removeIf(с -> с == null);
        if (filters.isEmpty()) return "";

        return " where " + String.join(" and ", filters);
    }

    private String appendOrderByClause(ProductSearchParams params) {
        if (params.getOrderBy() == null) return "";
        return " order by " + params.getOrderBy();
    }

    private String appendPaginationClause(ProductSearchParams params) {
        if (params.getPage() == null || params.getPageSize() == null) {
            return "";
        }
        return " limit " + params.getPageSize() + " offset " + params.getPage() * params.getPageSize();
    }

    private String buildLike(String field, String value) {
        if (value == null || value.isBlank()) return null;
        return field + " like '%" + value + "%'";
    }

    private String buildMin(String field, Number value) {
        if (value == null) return null;
        return field + " >= " + value;
    }

    private String buildMax(String field, Number value) {
        if (value == null) return null;
        return field + " <= " + value;
    }

}
