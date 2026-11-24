package com.mycompany.myostockmanagement.dao;

import com.mycompany.myostockmanagement.DBConnection;
import java.sql.*;
import java.util.*;

public class UserDAO {
    public record User(int id, String employeeId, String username, String fullName, String role) {}
    public List<User> findAll() throws SQLException {
        List<User> rows=new ArrayList<>();
        String sql="SELECT user_id, employee_id, username, f_name, role FROM users ORDER BY user_id";
        try(Connection c=DBConnection.getConnection(); PreparedStatement p=c.prepareStatement(sql); ResultSet r=p.executeQuery()){
            while(r.next()) rows.add(new User(r.getInt("user_id"),r.getString("employee_id"),r.getString("username"),r.getString("f_name"),r.getString("role")));
        } return rows;
    }
    public void insert(String employeeId,String username,String password,String fullName,String role) throws SQLException {
        String sql="INSERT INTO users (employee_id, username, password, f_name, role) VALUES (?,?,?,?,?)";
        try(Connection c=DBConnection.getConnection(); PreparedStatement p=c.prepareStatement(sql)){
            p.setString(1,employeeId.trim());p.setString(2,username.trim());p.setString(3,password);p.setString(4,fullName.trim());p.setString(5,role);p.executeUpdate();
        }
    }
    public void update(int id,String employeeId,String username,String password,String fullName,String role) throws SQLException {
        String sql = password == null || password.isBlank()
            ? "UPDATE users SET employee_id=?, username=?, f_name=?, role=? WHERE user_id=?"
            : "UPDATE users SET employee_id=?, username=?, password=?, f_name=?, role=? WHERE user_id=?";
        try(Connection c=DBConnection.getConnection(); PreparedStatement p=c.prepareStatement(sql)){
            p.setString(1,employeeId.trim());p.setString(2,username.trim());
            if(password == null || password.isBlank()){p.setString(3,fullName.trim());p.setString(4,role);p.setInt(5,id);}
            else {p.setString(3,password);p.setString(4,fullName.trim());p.setString(5,role);p.setInt(6,id);}
            if(p.executeUpdate()!=1) throw new SQLException("User not found.");
        }
    }
    public void delete(int id) throws SQLException {
        try(Connection c=DBConnection.getConnection(); PreparedStatement p=c.prepareStatement("DELETE FROM users WHERE user_id=?")){
            p.setInt(1,id); if(p.executeUpdate()!=1) throw new SQLException("User not found.");
        }
    }
}
