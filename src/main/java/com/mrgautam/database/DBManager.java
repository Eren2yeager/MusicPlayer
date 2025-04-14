package com.mrgautam.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBManager {
    private static final String URL = "jdbc:mysql://localhost:3306/musicdb";
    private static final String USER = "root";
    private static final String PASSWORD = "*#Sam4321#*"; // replace this

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void testConnection() {
        try (Connection conn = getConnection()) {
            System.out.println("✅ Connected to MySQL successfully!");
        } catch (SQLException e) {
            System.out.println("❌ MySQL connection failed!");
            e.printStackTrace();
        }
    }
}
