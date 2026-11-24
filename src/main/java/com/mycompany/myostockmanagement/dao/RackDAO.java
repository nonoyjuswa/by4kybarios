package com.mycompany.myostockmanagement.dao;

import com.mycompany.myostockmanagement.DBConnection;
import java.sql.*;
import java.util.*;

public class RackDAO {
    public record Rack(int id, String code, String zone, String name, String status) {}
    public List<Rack> findActive() throws SQLException {
        List<Rack> rows = new ArrayList<>();
        String sql = "SELECT rack_id, rack_code, zone, rack_name, status FROM racks ORDER BY zone, rack_code";
        try (Connection c=DBConnection.getConnection(); PreparedStatement p=c.prepareStatement(sql); ResultSet r=p.executeQuery()) {
            while(r.next()) rows.add(new Rack(r.getInt("rack_id"),r.getString("rack_code"),r.getString("zone"),r.getString("rack_name"),r.getString("status")));
        }
        return rows;
    }
    public int findActiveIdByCode(Connection c, String code) throws SQLException {
        try (PreparedStatement p=c.prepareStatement("SELECT rack_id FROM racks WHERE rack_code=? AND status='Active'")) {
            p.setString(1,code.trim()); try(ResultSet r=p.executeQuery()){ if(r.next()) return r.getInt(1); }
        }
        throw new SQLException("Rack not found or inactive: " + code);
    }
}
