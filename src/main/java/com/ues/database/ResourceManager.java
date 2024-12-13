package com.ues.database;

import reactor.core.publisher.Mono;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class ResourceManager {

    public ResourceManager() {
    }

    public static Mono<Boolean> createData(String tableName, Map<String, String> data) {
        return Mono.fromCallable(() -> {
            try (Connection connection = DatabaseConfig.getConnection()) {
                System.out.println("Checking if table exists: " + tableName);
                if (!tableExists(connection, tableName)) {
                    System.out.println("Table does not exist, creating table: " + tableName);
                    createTable(connection, tableName, data);
                }

                StringBuilder sql = new StringBuilder("INSERT INTO ").append(tableName).append(" (");
                StringBuilder placeholders = new StringBuilder("VALUES (");

                for (String key : data.keySet()) {
                    sql.append(key).append(",");
                    placeholders.append("?,");
                }

                sql.setLength(sql.length() - 1);
                placeholders.setLength(placeholders.length() - 1);

                sql.append(") ").append(placeholders).append(")");

                System.out.println("Executing SQL: " + sql.toString());

                try (PreparedStatement statement = connection.prepareStatement(sql.toString())) {
                    int index = 1;
                    List<String> parameters = new ArrayList<>();
                    for (String value : data.values()) {
                        statement.setString(index++, value);
                        parameters.add(value);
                    }

                    String boundSql = sql.toString();
                    for (String param : parameters) {
                        boundSql = boundSql.replaceFirst("\\?", "'" + param + "'");
                    }

                    System.out.println("Bound SQL: " + boundSql);

                    statement.executeUpdate();
                    return true;
                } catch (SQLException e) {
                    System.err.println("Database error: " + e.getMessage());
                    return false;
                }
            }
        });
    }

    private static boolean tableExists(Connection connection, String tableName) throws SQLException {
        String query = "SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = ? AND TABLE_SCHEMA = DATABASE()";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, tableName);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    private static void createTable(Connection connection, String tableName, Map<String, String> data) throws SQLException {
        StringBuilder sql = new StringBuilder("CREATE TABLE ").append(tableName).append(" (");
        sql.append("id INT PRIMARY KEY AUTO_INCREMENT,");
        for (String key : data.keySet()) {
            sql.append(key).append(" VARCHAR(255),");
        }
        sql.setLength(sql.length() - 1); // Remove last comma
        sql.append(")");

        System.out.println("Creating table with SQL: " + sql.toString());

        try (PreparedStatement statement = connection.prepareStatement(sql.toString())) {
            statement.execute();
        }
    }

    public static Mono<Boolean> updateData(String tableName, Map<String, String> data, String condition) {
        return Mono.fromCallable(() -> {
            StringBuilder sql = new StringBuilder("UPDATE ").append(tableName).append(" SET ");

            for (String key : data.keySet()) {
                sql.append(key).append(" = ?,");
            }

            sql.setLength(sql.length() - 1); // Remove last comma
            sql.append(" WHERE ").append(condition);

            try (Connection connection = DatabaseConfig.getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql.toString())) {

                int index = 1;
                for (String value : data.values()) {
                    statement.setString(index++, value);
                }

                statement.executeUpdate();
                return true;
            } catch (SQLException e) {
                System.err.println("Database error: " + e.getMessage());
                return false;
            }
        });
    }

    public static Mono<Boolean> deleteData(String tableName, String condition) {
        return Mono.fromCallable(() -> {
            String sql = "DELETE FROM " + tableName + " WHERE " + condition;

            try (Connection connection = DatabaseConfig.getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql)) {

                statement.executeUpdate();
                return true;
            } catch (SQLException e) {
                System.err.println("Database error: " + e.getMessage());
                return false;
            }
        });
    }

    public static Mono<List<Map<String, String>>> getData(String tableName, String condition) {
        return Mono.fromCallable(() -> {
            List<Map<String, String>> result = new ArrayList<>();
            String sql = "SELECT * FROM " + tableName + " WHERE " + condition;

            try (Connection connection = DatabaseConfig.getConnection();
                 PreparedStatement statement = connection.prepareStatement(sql);
                 ResultSet resultSet = statement.executeQuery()) {

                int columnCount = resultSet.getMetaData().getColumnCount();
                while (resultSet.next()) {
                    Map<String, String> row = new HashMap<>();
                    for (int i = 1; i <= columnCount; i++) {
                        row.put(resultSet.getMetaData().getColumnName(i), resultSet.getString(i));
                    }
                    result.add(row);
                }
            } catch (SQLException e) {
                System.err.println("Database error: " + e.getMessage());
            }

            return result;
        });
    }
}
