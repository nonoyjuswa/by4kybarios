package com.mycompany.myostockmanagement.service;

import com.mycompany.myostockmanagement.dao.ProductDAO;
import java.sql.SQLException;
import java.util.List;

public class ProductService {
    private final ProductDAO dao = new ProductDAO();
    public List<ProductDAO.Product> findAll() throws SQLException { return dao.findAll(); }
    
    
    public int create(String name,String rack,int quantity,int reorder,String employeeId) throws SQLException {
        validate(name,rack,quantity,reorder);
        if(employeeId==null||employeeId.isBlank()) throw new IllegalArgumentException("Signed-in employee ID is missing; log in again.");
        return dao.insert(name,rack,quantity,reorder,employeeId);
    }
    public void update(int id,String name,String rack,int quantity,int reorder) throws SQLException {
        if(id<=0) throw new IllegalArgumentException("Select a product first."); validate(name,rack,quantity,reorder); dao.update(id,name,rack,quantity,reorder);
    }
    public void delete(int id) throws SQLException { if(id<=0) throw new IllegalArgumentException("Select a product first."); dao.delete(id); }
    
    private void validate(String name,String rack,int quantity,int reorder) {
        if(name==null||name.isBlank()) throw new IllegalArgumentException("Product name is required.");
        if(rack==null||rack.isBlank()) throw new IllegalArgumentException("Rack location is required.");
        if(quantity<0) throw new IllegalArgumentException("Quantity cannot be negative.");
        if(reorder<0) throw new IllegalArgumentException("Reorder level cannot be negative.");
    }
}