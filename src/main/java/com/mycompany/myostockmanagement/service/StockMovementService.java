package com.mycompany.myostockmanagement.service;

import com.mycompany.myostockmanagement.DBConnection;
import com.mycompany.myostockmanagement.dao.RackDAO;
import java.sql.*;

/** Transactional stock movement service. UI calls this; SQL stays out of the UI. */
public class StockMovementService {
    private final RackDAO rackDAO = new RackDAO();

    public void recordStockIn(int productId,int quantity) throws SQLException {
        throw new IllegalArgumentException("Use recordStockIn(productId, rackCode, employeeId, quantity, referenceNo, remarks).");
    }
    
    public void recordStockOut(int productId,int quantity) throws SQLException {
        throw new IllegalArgumentException("Use recordStockOut(productId, rackCode, employeeId, quantity, referenceNo, remarks).");
    }

    public void recordStockIn(int productId,String rackCode,String employeeId,int quantity,String referenceNo,String remarks)throws SQLException{
        validate(productId,rackCode,employeeId,quantity);
        try(Connection c=DBConnection.getConnection()){
            c.setAutoCommit(false);
            try{
                int userId=findUserId(c,employeeId); int rackId=rackDAO.findActiveIdByCode(c,rackCode); ensureProduct(c,productId);
                int inventoryId=-1;
                try(PreparedStatement p=c.prepareStatement("SELECT inventory_id FROM inventory WHERE product_id=? AND rack_id=? LIMIT 1 FOR UPDATE")){p.setInt(1,productId);p.setInt(2,rackId);try(ResultSet r=p.executeQuery()){if(r.next())inventoryId=r.getInt(1);}}
                if(inventoryId>0){try(PreparedStatement p=c.prepareStatement("UPDATE inventory SET quantity=quantity+? WHERE inventory_id=?")){p.setInt(1,quantity);p.setInt(2,inventoryId);p.executeUpdate();}}
                else{try(PreparedStatement p=c.prepareStatement("INSERT INTO inventory(product_id,rack_id,quantity) VALUES(?,?,?)")){p.setInt(1,productId);p.setInt(2,rackId);p.setInt(3,quantity);p.executeUpdate();}}
                try(PreparedStatement p=c.prepareStatement("UPDATE products SET quantity=quantity+? WHERE product_id=?")){p.setInt(1,quantity);p.setInt(2,productId);if(p.executeUpdate()!=1)throw new SQLException("Product not found.");}
                insertMovement(c,productId,rackId,userId,"IN",quantity,referenceNo,remarks);c.commit();
            }catch(SQLException|RuntimeException ex){rollback(c,ex);throw ex;}finally{try{c.setAutoCommit(true);}catch(SQLException ignored){}}
        }
    }

    public void recordStockOut(int productId,String rackCode,String employeeId,int quantity,String referenceNo,String remarks)throws SQLException{
        validate(productId,rackCode,employeeId,quantity);
        try(Connection c=DBConnection.getConnection()){
            c.setAutoCommit(false);
            try{
                int userId=findUserId(c,employeeId); int rackId=rackDAO.findActiveIdByCode(c,rackCode);
                int inventoryId,available;
                try(PreparedStatement p=c.prepareStatement("SELECT inventory_id,quantity FROM inventory WHERE product_id=? AND rack_id=? LIMIT 1 FOR UPDATE")){p.setInt(1,productId);p.setInt(2,rackId);try(ResultSet r=p.executeQuery()){if(!r.next())throw new SQLException("No inventory record for this product in rack "+rackCode+".");inventoryId=r.getInt(1);available=r.getInt(2);}}
                if(available<quantity)throw new SQLException("Insufficient stock in rack "+rackCode+". Available: "+available+".");
                try(PreparedStatement p=c.prepareStatement("UPDATE inventory SET quantity=quantity-? WHERE inventory_id=?")){p.setInt(1,quantity);p.setInt(2,inventoryId);p.executeUpdate();}
                try(PreparedStatement p=c.prepareStatement("UPDATE products SET quantity=quantity-? WHERE product_id=? AND quantity>=?")){p.setInt(1,quantity);p.setInt(2,productId);p.setInt(3,quantity);if(p.executeUpdate()!=1)throw new SQLException("Insufficient total product stock or product not found.");}
                insertMovement(c,productId,rackId,userId,"OUT",quantity,referenceNo,remarks);c.commit();
            }catch(SQLException|RuntimeException ex){rollback(c,ex);throw ex;}finally{try{c.setAutoCommit(true);}catch(SQLException ignored){}}
        }
    }

    private int findUserId(Connection c,String employeeId)throws SQLException{try(PreparedStatement p=c.prepareStatement("SELECT user_id FROM users WHERE employee_id=?")){p.setString(1,employeeId.trim());try(ResultSet r=p.executeQuery()){if(r.next())return r.getInt(1);}}throw new SQLException("Signed-in employee not found: "+employeeId);}
    private void ensureProduct(Connection c,int id)throws SQLException{try(PreparedStatement p=c.prepareStatement("SELECT product_id FROM products WHERE product_id=?")){p.setInt(1,id);try(ResultSet r=p.executeQuery()){if(r.next())return;}}throw new SQLException("Product ID not found: "+id);}
    private void insertMovement(Connection c,int productId,int rackId,int userId,String type,int qty,String ref,String remarks)throws SQLException{String sql="INSERT INTO stock_movements(product_id,rack_id,user_id,movement_type,quantity,reference_no,remarks) VALUES(?,?,?,?,?,?,?)";try(PreparedStatement p=c.prepareStatement(sql)){p.setInt(1,productId);p.setInt(2,rackId);p.setInt(3,userId);p.setString(4,type);p.setInt(5,qty);p.setString(6,blankToNull(ref));p.setString(7,blankToNull(remarks));p.executeUpdate();}}
    private String blankToNull(String s){return s==null||s.isBlank()?null:s.trim();}
    private void validate(int id,String rack,String employee,int qty){if(id<=0)throw new IllegalArgumentException("Select a valid product.");if(rack==null||rack.isBlank())throw new IllegalArgumentException("Rack code is required.");if(employee==null||employee.isBlank())throw new IllegalArgumentException("Signed-in employee ID is missing; log in again.");if(qty<=0)throw new IllegalArgumentException("Quantity must be greater than zero.");}
    private void rollback(Connection c,Exception original){try{c.rollback();}catch(SQLException ex){original.addSuppressed(ex);}}
    public boolean testConnection()throws SQLException{try(Connection c=DBConnection.getConnection()){return c.isValid(5);}}
}
