package com.mrgautam.database;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBManager {
    private static final String URL = "jdbc:mysql://localhost:3306/musicdb";
    private static final String USER = "root";
    private static final String PASSWORD = "*#Sam4321#*"; // replace this

    public static Connection getConnection(){
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (Exception e) {
            return null;
        }
    }


    
}
