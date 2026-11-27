package com.mycompany.myostockmanagement.dao;

import com.mycompany.myostockmanagement.DBConnection;
import com.mycompany.myostockmanagement.model.Adjustment;
import com.mycompany.myostockmanagement.model.AdjustmentLine;

import java.sql.*;
import java.util.*;

public class StockAdjustmentDAO {
    public record UserRef(int id, String role) {}

    public UserRef findUser(String employeeId) throws SQLException {
        String sql = "SELECT user_id, role FROM users WHERE employee_id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, employeeId == null ? "" : employeeId.trim());
            try (ResultSet r = p.executeQuery()) {
                if (r.next()) return new UserRef(r.getInt("user_id"), r.getString("role"));
            }
        }
        throw new SQLException("Signed-in employee not found: " + employeeId);
    }

    public List<Object[]> findListRows() throws SQLException {
        String sql = """
            SELECT sa.reference_no,
                   DATE_FORMAT(sa.created_at, '%d %b %Y') AS adjustment_date,
                   r.rack_code,
                   u.f_name AS counted_name,
                   (SELECT COUNT(*) FROM stock_adjustment_lines sal WHERE sal.adjustment_id=sa.adjustment_id) AS line_count,
                   (SELECT COALESCE(SUM(sal.variance), 0) FROM stock_adjustment_lines sal WHERE sal.adjustment_id=sa.adjustment_id) AS net_variance,
                   sa.reason, sa.status,
                   COALESCE(ar.f_name, '—') AS approved_name
            FROM stock_adjustments sa
            JOIN racks r ON r.rack_id=sa.rack_id
            JOIN users u ON u.user_id=sa.counted_by
            LEFT JOIN users ar ON ar.user_id=sa.approved_by
            GROUP BY sa.adjustment_id, sa.reference_no, sa.created_at, r.rack_code, u.f_name,
                     sa.reason, sa.status, ar.f_name
            ORDER BY sa.created_at DESC, sa.adjustment_id DESC
            """;
        List<Object[]> rows = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement p = c.prepareStatement(sql);
             ResultSet r = p.executeQuery()) {
            while (r.next()) {
                int variance = r.getInt("net_variance");
                rows.add(new Object[]{
                    r.getString("reference_no"), r.getString("adjustment_date"),
                    r.getString("rack_code"), r.getString("counted_name"),
                    r.getInt("line_count"), (variance > 0 ? "+" : "") + variance,
                    r.getString("reason"), r.getString("status"), r.getString("approved_name")
                });
            }
        }
        return rows;
    }

    public Adjustment findByReference(String ref) throws SQLException {
        String header = """
            SELECT sa.adjustment_id, sa.reference_no, r.rack_code, sa.reason, sa.explanation,
                   sa.status, sa.counted_by, cu.f_name AS counted_by_name,
                   ar.f_name AS approved_by_name, sa.review_note
            FROM stock_adjustments sa
            JOIN racks r ON r.rack_id=sa.rack_id
            JOIN users cu ON cu.user_id=sa.counted_by
            LEFT JOIN users ar ON ar.user_id=sa.approved_by
            WHERE sa.reference_no=?
            """;
        try (Connection c = DBConnection.getConnection();
             PreparedStatement p = c.prepareStatement(header)) {
            p.setString(1, ref == null ? "" : ref.trim());
            try (ResultSet r = p.executeQuery()) {
                if (!r.next()) throw new SQLException("Adjustment not found: " + ref);
                int id = r.getInt("adjustment_id");
                List<AdjustmentLine> lines = new ArrayList<>();
                try (PreparedStatement lp = c.prepareStatement("""
                        SELECT sal.product_id, p.product_name, sal.system_qty, sal.counted_qty
                        FROM stock_adjustment_lines sal
                        JOIN products p ON p.product_id=sal.product_id
                        WHERE sal.adjustment_id=? ORDER BY sal.line_id
                        """)) {
                    lp.setInt(1, id);
                    try (ResultSet lr = lp.executeQuery()) {
                        while (lr.next()) lines.add(new AdjustmentLine(
                            lr.getInt("product_id"), lr.getString("product_name"),
                            lr.getInt("system_qty"), lr.getInt("counted_qty")));
                    }
                }
                return new Adjustment(id, r.getString("reference_no"), r.getString("rack_code"),
                    r.getString("reason"), r.getString("explanation"), r.getString("status"),
                    r.getInt("counted_by"), r.getString("counted_by_name"),
                    r.getString("approved_by_name"), r.getString("review_note"), lines);
            }
        }
    }

    public int findSystemQty(int productId, String rackCode) throws SQLException {
        String sql = """
            SELECT COALESCE(SUM(i.quantity), 0) AS qty
            FROM inventory i JOIN racks r ON r.rack_id=i.rack_id
            WHERE i.product_id=? AND r.rack_code=? AND r.status='Active'
            """;
        try (Connection c = DBConnection.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setInt(1, productId);
            p.setString(2, rackCode == null ? "" : rackCode.trim());
            try (ResultSet r = p.executeQuery()) {
                return r.next() ? r.getInt("qty") : 0;
            }
        }
    }

    public int saveDraft(int id, String rackCode, String reason, String explanation,
                         int userId, List<AdjustmentLine> lines) throws SQLException {
        try (Connection c = DBConnection.getConnection()) {
            c.setAutoCommit(false);
            try {
                int rackId = new RackDAO().findActiveIdByCode(c, rackCode);
                int adjustmentId;
                if (id == 0) {
                    String tmp = "TMP-" + UUID.randomUUID().toString().substring(0, 8);
                    try (PreparedStatement p = c.prepareStatement("""
                            INSERT INTO stock_adjustments
                            (reference_no, rack_id, reason, explanation, status, counted_by)
                            VALUES (?, ?, ?, ?, 'Draft', ?)
                            """, Statement.RETURN_GENERATED_KEYS)) {
                        p.setString(1, tmp);
                        p.setInt(2, rackId);
                        p.setString(3, reason);
                        p.setString(4, explanation);
                        p.setInt(5, userId);
                        p.executeUpdate();
                        try (ResultSet keys = p.getGeneratedKeys()) {
                            if (!keys.next()) throw new SQLException("Could not create adjustment.");
                            adjustmentId = keys.getInt(1);
                        }
                    }
                    String reference = "SA-" + java.time.Year.now().getValue() + "-" +
                        String.format("%04d", adjustmentId);
                    try (PreparedStatement p = c.prepareStatement(
                            "UPDATE stock_adjustments SET reference_no=? WHERE adjustment_id=?")) {
                        p.setString(1, reference);
                        p.setInt(2, adjustmentId);
                        executeOne(p, "Could not set adjustment reference.");
                    }
                } else {
                    adjustmentId = id;
                    try (PreparedStatement p = c.prepareStatement("""
                            UPDATE stock_adjustments
                            SET rack_id=?, reason=?, explanation=?
                            WHERE adjustment_id=? AND status='Draft'
                            """)) {
                        p.setInt(1, rackId);
                        p.setString(2, reason);
                        p.setString(3, explanation);
                        p.setInt(4, adjustmentId);
                        executeOne(p, "Only a Draft can be edited.");
                    }
                    try (PreparedStatement p = c.prepareStatement(
                            "DELETE FROM stock_adjustment_lines WHERE adjustment_id=?")) {
                        p.setInt(1, adjustmentId);
                        p.executeUpdate();
                    }
                }
                try (PreparedStatement p = c.prepareStatement("""
                        INSERT INTO stock_adjustment_lines
                        (adjustment_id, product_id, system_qty, counted_qty, variance)
                        VALUES (?, ?, ?, ?, ?)
                        """)) {
                    for (AdjustmentLine line : lines) {
                        p.setInt(1, adjustmentId);
                        p.setInt(2, line.productId());
                        p.setInt(3, line.systemQty());
                        p.setInt(4, line.countedQty());
                        p.setInt(5, line.variance());
                        p.addBatch();
                    }
                    p.executeBatch();
                }
                c.commit();
                return adjustmentId;
            } catch (SQLException | RuntimeException ex) {
                c.rollback();
                throw ex;
            } finally {
                c.setAutoCommit(true);
            }
        }
    }

    public void submit(int id) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement p = c.prepareStatement("""
                 UPDATE stock_adjustments SET status='Pending Approval'
                 WHERE adjustment_id=? AND status='Draft'
                 """)) {
            p.setInt(1, id);
            executeOne(p, "Only a Draft can be submitted.");
        }
    }

    public void approve(int id, int approverId) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement p = c.prepareStatement("""
                 UPDATE stock_adjustments
                 SET status='Approved', approved_by=?, approved_at=NOW()
                 WHERE adjustment_id=? AND status='Pending Approval' AND counted_by<>?
                 """)) {
            p.setInt(1, approverId);
            p.setInt(2, id);
            p.setInt(3, approverId);
            executeOne(p, "Only another supervisor can approve this adjustment.");
        }
    }

    public void reject(int id, int reviewerId, String note) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement p = c.prepareStatement("""
                 UPDATE stock_adjustments
                 SET status='Rejected', approved_by=?, approved_at=NOW(), review_note=?
                 WHERE adjustment_id=? AND status='Pending Approval' AND counted_by<>?
                 """)) {
            p.setInt(1, reviewerId);
            p.setString(2, note);
            p.setInt(3, id);
            p.setInt(4, reviewerId);
            executeOne(p, "Only another supervisor can reject this adjustment.");
        }
    }

    public void post(int id, int posterId) throws SQLException {
        try (Connection c = DBConnection.getConnection()) {
            c.setAutoCommit(false);
            try {
                String reference;
                String reason;
                int rackId;
                try (PreparedStatement p = c.prepareStatement("""
                        UPDATE stock_adjustments SET status='Posted', posted_at=NOW()
                        WHERE adjustment_id=? AND status='Approved'
                        """)) {
                    p.setInt(1, id);
                    executeOne(p, "Only an Approved adjustment can be posted.");
                }
                try (PreparedStatement p = c.prepareStatement("""
                        SELECT sa.reference_no, sa.reason, sa.rack_id
                        FROM stock_adjustments sa WHERE sa.adjustment_id=? FOR UPDATE
                        """)) {
                    p.setInt(1, id);
                    try (ResultSet r = p.executeQuery()) {
                        if (!r.next()) throw new SQLException("Adjustment not found.");
                        reference = r.getString("reference_no");
                        reason = r.getString("reason");
                        rackId = r.getInt("rack_id");
                    }
                }
                List<AdjustmentLine> lines = new ArrayList<>();
                try (PreparedStatement p = c.prepareStatement("""
                        SELECT sal.product_id, p.product_name, sal.system_qty, sal.counted_qty, sal.variance
                        FROM stock_adjustment_lines sal
                        JOIN products p ON p.product_id=sal.product_id
                        WHERE sal.adjustment_id=? ORDER BY sal.line_id
                        """)) {
                    p.setInt(1, id);
                    try (ResultSet r = p.executeQuery()) {
                        while (r.next()) lines.add(new AdjustmentLine(
                            r.getInt("product_id"), r.getString("product_name"),
                            r.getInt("system_qty"), r.getInt("counted_qty")));
                    }
                }
                for (AdjustmentLine line : lines) {
                    int variance = line.variance();
                    if (variance == 0) continue;
                    if (variance > 0) {
                        try (PreparedStatement p = c.prepareStatement("""
                                INSERT INTO inventory (product_id, rack_id, quantity)
                                VALUES (?, ?, ?)
                                ON DUPLICATE KEY UPDATE quantity=quantity+VALUES(quantity)
                                """)) {
                            p.setInt(1, line.productId());
                            p.setInt(2, rackId);
                            p.setInt(3, variance);
                            p.executeUpdate();
                        }
                    } else {
                        int decrease = -variance;
                        try (PreparedStatement p = c.prepareStatement("""
                                UPDATE inventory SET quantity=quantity-?
                                WHERE product_id=? AND rack_id=? AND quantity>=?
                                """)) {
                            p.setInt(1, decrease);
                            p.setInt(2, line.productId());
                            p.setInt(3, rackId);
                            p.setInt(4, decrease);
                            executeOne(p, "stock changed since the count");
                        }
                    }
                    try (PreparedStatement p = c.prepareStatement("""
                            UPDATE products SET quantity=quantity+?
                            WHERE product_id=? AND quantity+?>=0
                            """)) {
                        p.setInt(1, variance);
                        p.setInt(2, line.productId());
                        p.setInt(3, variance);
                        executeOne(p, "Stock could not be updated for " + line.productName());
                    }
                    try (PreparedStatement p = c.prepareStatement("""
                            INSERT INTO stock_movements
                            (product_id, rack_id, user_id, movement_type, quantity, reference_no, remarks)
                            VALUES (?, ?, ?, ?, ?, ?, ?)
                            """)) {
                        p.setInt(1, line.productId());
                        p.setInt(2, rackId);
                        p.setInt(3, posterId);
                        p.setString(4, variance > 0 ? "IN" : "OUT");
                        p.setInt(5, Math.abs(variance));
                        p.setString(6, reference);
                        p.setString(7, "Stock adjustment: " + reason);
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

    private void executeOne(PreparedStatement p, String message) throws SQLException {
        if (p.executeUpdate() != 1) throw new SQLException(message);
    }
}
