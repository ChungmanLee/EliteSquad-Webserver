package com.ues;

import com.ues.database.DatabaseConfig;

public class MainServer {
    public static void main(String[] args) {
        DatabaseConfig.initializeDatabase();
        NioHttpServer httpServer = new NioHttpServer();
        new Thread(httpServer).start();
        SecureServer secureServer = new SecureServer();
        new Thread(secureServer).start();
    }
}