package com.mycompany.myostockmanagement.dao;

import com.mycompany.myostockmanagement.DBConnection;
import java.sql.*;
import java.util.*;

public class StockMovementDAO {
    public Object[][] findLatest(int limit) throws SQLException {
        List<Object[]> rows=new ArrayList<>();
        String sql="SELECT DATE_FORMAT(sm.movement_date,'%Y-%m-%d') move_date, DATE_FORMAT(sm.movement_date,'%H:%i:%s') move_time, sm.movement_type, p.product_name, sm.quantity, r.rack_code, u.f_name FROM stock_movements sm JOIN products p ON sm.product_id=p.product_id JOIN racks r ON sm.rack_id=r.rack_id JOIN users u ON sm.user_id=u.user_id ORDER BY sm.movement_date DESC LIMIT ?";
        try(Connection c=DBConnection.getConnection(); PreparedStatement p=c.prepareStatement(sql)){
            p.setInt(1,Math.max(1,Math.min(limit,200))); try(ResultSet r=p.executeQuery()){
                while(r.next()) rows.add(new Object[]{r.getString("move_date"),r.getString("move_time"),r.getString("movement_type"),r.getString("product_name"),r.getInt("quantity"),r.getString("rack_code"),r.getString("f_name")});
            }
        } return rows.toArray(Object[][]::new);
    }
}
