package com.mycompany.myostockmanagement.dao;

import com.mycompany.myostockmanagement.DBConnection;
import java.sql.*;
import java.util.*;

/** Child records: each inventory row belongs to one product and one rack. */
public class InventoryDAO {
    public List<Object[]> findAll() throws SQLException {
        List<Object[]> rows=new ArrayList<>();
        String sql="SELECT i.inventory_id,p.product_id,p.product_name,r.rack_code,i.quantity,i.updated_at FROM inventory i JOIN products p ON i.product_id=p.product_id JOIN racks r ON i.rack_id=r.rack_id ORDER BY p.product_name,r.rack_code";
        try(Connection c=DBConnection.getConnection();PreparedStatement p=c.prepareStatement(sql);ResultSet r=p.executeQuery()){
            while(r.next()) rows.add(new Object[]{r.getInt("inventory_id"),r.getInt("product_id"),r.getString("product_name"),r.getString("rack_code"),r.getInt("quantity"),r.getTimestamp("updated_at")});
        }return rows;
    }
}
