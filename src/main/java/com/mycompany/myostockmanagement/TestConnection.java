package com.mycompany.myostockmanagement;

import java.sql.Connection;
import java.sql.SQLException;

public class TestConnection {

    public static void main(String[] args) {
        try (Connection con = DBConnection.getConnection()) {
            System.out.println("HikariCP connected successfully!");
            System.out.println("Database: "
                + con.getCatalog());
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.closePool();
        }
    }
}
