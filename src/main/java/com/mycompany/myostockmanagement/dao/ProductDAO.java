package com.mycompany.myostockmanagement.dao;

import com.mycompany.myostockmanagement.DBConnection;
import java.sql.*;
import java.util.*;

/** Data access for products. Keeps products, inventory and stock_movements in sync. */
public class ProductDAO {
    public record Product(int id, String name, String rackLocation, int quantity, int reorderLevel) {}

    public List<Product> findAll() throws SQLException {
        String sql = "SELECT product_id, product_name, rack_location, quantity, reorder_level FROM products ORDER BY product_id";
        List<Product> rows = new ArrayList<>();
        try (Connection c = DBConnection.getConnection(); PreparedStatement p = c.prepareStatement(sql); ResultSet r = p.executeQuery()) {
            while (r.next()) rows.add(new Product(r.getInt("product_id"), r.getString("product_name"), r.getString("rack_location"), r.getInt("quantity"), r.getInt("reorder_level")));
        }
        return rows;
    }

   public int insert(String name, String rackLocation, int quantity, int reorderLevel, String employeeId) throws SQLException {
    try (Connection c = DBConnection.getConnection()) {
        c.setAutoCommit(false);
        try {
            // 1. The rack must exist and be Active
            int rackId = new RackDAO().findActiveIdByCode(c, rackLocation);

            // 2. Does a product with this name already exist? Lock it while we work.
            Integer existingId = null;
            try (PreparedStatement p = c.prepareStatement(
                    "SELECT product_id FROM products WHERE product_name=? FOR UPDATE")) {
                p.setString(1, name.trim());
                try (ResultSet r = p.executeQuery()) {
                    if (r.next()) existingId = r.getInt(1);
                }
            }

            int productId;
            if (existingId != null) {
                // ---- EXISTING PRODUCT: add to it ----
                productId = existingId;

                try (PreparedStatement p = c.prepareStatement(
                        "UPDATE products SET quantity = quantity + ? WHERE product_id=?")) {
                    p.setInt(1, quantity);
                    p.setInt(2, productId);
                    p.executeUpdate();
                }

                // add to the same rack row, or create it if this product isn't in that rack yet
                try (PreparedStatement p = c.prepareStatement(
                        "INSERT INTO inventory (product_id, rack_id, quantity) VALUES (?, ?, ?) " +
                        "ON DUPLICATE KEY UPDATE quantity = quantity + VALUES(quantity)")) {
                    p.setInt(1, productId);
                    p.setInt(2, rackId);
                    p.setInt(3, quantity);
                    p.executeUpdate();
                }
            } else {
                
                // ---- NEW PRODUCT: same as before ----
                String sql = "INSERT INTO products (product_code, product_name, rack_location, quantity, reorder_level) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement p = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    p.setString(1, "TMP-" + UUID.randomUUID().toString().substring(0, 8));
                    p.setString(2, name.trim());
                    p.setString(3, rackLocation.trim());
                    p.setInt(4, quantity);
                    p.setInt(5, reorderLevel);
                    p.executeUpdate();
                    try (ResultSet keys = p.getGeneratedKeys()) {
                        keys.next();
                        productId = keys.getInt(1);
                    }
                }

                try (PreparedStatement p = c.prepareStatement("UPDATE products SET product_code=? WHERE product_id=?")) {
                    p.setString(1, String.format("SKU-%03d", productId));
                    p.setInt(2, productId);
                    p.executeUpdate();
                }

                try (PreparedStatement p = c.prepareStatement(
                        "INSERT INTO inventory (product_id, rack_id, quantity) VALUES (?, ?, ?)")) {
                    p.setInt(1, productId);
                    p.setInt(2, rackId);
                    p.setInt(3, quantity);
                    p.executeUpdate();
                }
            }

            // 3. Log the movement (for both new and existing products)
            if (quantity > 0) {
                int userId;
                try (PreparedStatement p = c.prepareStatement("SELECT user_id FROM users WHERE employee_id=?")) {
                    p.setString(1, employeeId == null ? "" : employeeId.trim());
                    try (ResultSet r = p.executeQuery()) {
                        if (!r.next()) throw new SQLException("Signed-in employee not found: " + employeeId);
                        userId = r.getInt(1);
                    }
                }
                try (PreparedStatement p = c.prepareStatement(
                        "INSERT INTO stock_movements (product_id, rack_id, user_id, movement_type, quantity, remarks) " +
                        "VALUES (?, ?, ?, 'IN', ?, ?)")) {
                    p.setInt(1, productId);
                    p.setInt(2, rackId);
                    p.setInt(3, userId);
                    p.setInt(4, quantity);
                    p.setString(5, existingId != null ? "Stock added from Inventory screen" : "Initial stock");
                    p.executeUpdate();
                }
            }

            c.commit();
            return productId;
        } catch (SQLException | RuntimeException ex) {
            c.rollback();
            throw ex;
        } finally {
            c.setAutoCommit(true);
        }
    }
}

    public void update(int id, String name, String rackLocation, int quantity, int reorderLevel) throws SQLException {
        try (Connection c = DBConnection.getConnection()) {
            c.setAutoCommit(false);
            try {
                int rackId = new RackDAO().findActiveIdByCode(c, rackLocation);

                String sql = "UPDATE products SET product_name=?, rack_location=?, quantity=?, reorder_level=? WHERE product_id=?";
               
            try (PreparedStatement p = c.prepareStatement(sql)) {
                  p.setString(1, name.trim());
                  p.setString(2, rackLocation.trim());
                  p.setInt(3, quantity);
                  p.setInt(4, reorderLevel);
                  p.setInt(5, id);
                  if (p.executeUpdate() != 1) throw new SQLException("Product was not found or was not updated.");
              } catch (SQLIntegrityConstraintViolationException dup) {
                  throw new SQLException("A product named \"" + name.trim() + "\" already exists.");
}

                List<Integer> inventoryIds = new ArrayList<>();
                try (PreparedStatement p = c.prepareStatement("SELECT inventory_id FROM inventory WHERE product_id=? FOR UPDATE")) {
                    p.setInt(1, id);
                    try (ResultSet r = p.executeQuery()) {
                        while (r.next()) inventoryIds.add(r.getInt(1));
                    }
                }
                
                

                if (inventoryIds.size() > 1) {
                    throw new SQLException("This product is stored in several racks. Use Stock In / Stock Out to change quantities per rack.");
                } else if (inventoryIds.size() == 1) {
                    try (PreparedStatement p = c.prepareStatement("UPDATE inventory SET rack_id=?, quantity=? WHERE inventory_id=?")) {
                        p.setInt(1, rackId);
                        p.setInt(2, quantity);
                        p.setInt(3, inventoryIds.get(0));
                        p.executeUpdate();
                    }
                } else {
                    try (PreparedStatement p = c.prepareStatement("INSERT INTO inventory (product_id, rack_id, quantity) VALUES (?, ?, ?)")) {
                        p.setInt(1, id);
                        p.setInt(2, rackId);
                        p.setInt(3, quantity);
                        p.executeUpdate();
                    }
                }

                c.commit();
            } catch (SQLException | RuntimeException ex) {
                c.rollback();
                throw ex;
            } finally {
                c.setAutoCommit(true);
            }
        }
    }

    public void delete(int id) throws SQLException {
        try (Connection c = DBConnection.getConnection()) {
            c.setAutoCommit(false);
            try {
                try (PreparedStatement p = c.prepareStatement("SELECT COUNT(*) FROM stock_movements WHERE product_id=?")) {
                    p.setInt(1, id);
                    try (ResultSet r = p.executeQuery()) {
                        if (r.next() && r.getInt(1) > 0)
                            throw new SQLException("This product has movement history. Set its quantity to 0 instead of deleting it.");
                    }
                }
                try (PreparedStatement p = c.prepareStatement("DELETE FROM inventory WHERE product_id=?")) {
                    p.setInt(1, id);
                    p.executeUpdate();
                }
                try (PreparedStatement p = c.prepareStatement("DELETE FROM products WHERE product_id=?")) {
                    p.setInt(1, id);
                    if (p.executeUpdate() != 1) throw new SQLException("Product was not found.");
                }
                c.commit();
            } catch (SQLException | RuntimeException ex) {
                c.rollback();
                throw ex;
            } finally {
                c.setAutoCommit(true);
            }
        }
    }
}