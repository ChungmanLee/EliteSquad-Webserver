package com.ues.adminportal.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import javax.sql.DataSource;
import java.sql.Connection;

@RestController
public class DatabaseHealthCheckController {

    @Autowired
    private DataSource dataSource;

    @GetMapping("/db-health")
    public String checkDatabaseHealth() {
        try (Connection connection = dataSource.getConnection()) {
            return "Database connection is OK!";
        } catch (Exception e) {
            return "Failed to connect to the database: " + e.getMessage();
        }
    }
}