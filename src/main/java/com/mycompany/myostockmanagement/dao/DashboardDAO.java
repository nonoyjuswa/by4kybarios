package com.mycompany.myostockmanagement.dao;

import com.mycompany.myostockmanagement.DBConnection;
import java.sql.*;
import java.util.*;

public class DashboardDAO {
    public List<Object[]> findRestockingProducts() throws SQLException {
        List<Object[]> rows = new ArrayList<>();
        String sql = "SELECT product_name, rack_location, quantity, reorder_level FROM products WHERE quantity <= reorder_level ORDER BY quantity ASC";
        try (Connection c=DBConnection.getConnection(); PreparedStatement p=c.prepareStatement(sql); ResultSet r=p.executeQuery()) {
            while(r.next()) rows.add(new Object[]{r.getString("product_name"),r.getString("rack_location"),r.getInt("quantity"),"Needs Restocking"});
        }
        return rows;
    }
}
