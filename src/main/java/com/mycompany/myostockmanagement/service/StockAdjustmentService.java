package com.mycompany.myostockmanagement.service;

import com.mycompany.myostockmanagement.dao.ProductDAO;
import com.mycompany.myostockmanagement.dao.StockAdjustmentDAO;
import com.mycompany.myostockmanagement.model.Adjustment;
import com.mycompany.myostockmanagement.model.AdjustmentLine;

import java.sql.SQLException;
import java.util.List;

public class StockAdjustmentService {
    public static final String[] REASONS = {"Count discrepancy","Damaged goods","Loss","Found stock"};
    private final StockAdjustmentDAO dao = new StockAdjustmentDAO();

    public List<Object[]> findListRows() throws SQLException { return dao.findListRows(); }
    public Adjustment find(String reference) throws SQLException { return dao.findByReference(reference); }

    public int systemQty(int productId, String rackCode) throws SQLException {
        if (productId <= 0) throw new IllegalArgumentException("Select a product.");
        if (rackCode == null || rackCode.isBlank()) throw new IllegalArgumentException("Rack location is required.");
        return dao.findSystemQty(productId, rackCode);
    }

    public boolean isSupervisor(String employeeId) throws SQLException {
        if (employeeId == null || employeeId.isBlank()) return false;
        return "Admin".equalsIgnoreCase(dao.findUser(employeeId).role());
    }

    public int saveDraft(int id, String rackCode, String reason, String explanation,
                         String employeeId, List<AdjustmentLine> lines) throws SQLException {
        if (rackCode == null || rackCode.isBlank()) throw new IllegalArgumentException("Rack location is required.");
        String canonicalReason = canonicalReason(reason);
        if (explanation == null || explanation.isBlank()) throw new IllegalArgumentException("Explanation is required.");
        if (lines == null || lines.isEmpty()) throw new IllegalArgumentException("Add at least one count line.");
        for (AdjustmentLine line : lines) {
            if (line.countedQty() < 0) throw new IllegalArgumentException("Counted quantity cannot be negative.");
            if (line.variance() == 0) throw new IllegalArgumentException("No variance for \"" + line.productName() + "\".");
        }
        StockAdjustmentDAO.UserRef user = dao.findUser(employeeId);
        return dao.saveDraft(id, rackCode.trim(), canonicalReason, explanation.trim(), user.id(), lines);
    }

    public void submit(int id) throws SQLException {
        if (id <= 0) throw new IllegalArgumentException("Save the draft before submitting.");
        dao.submit(id);
    }

    public void approve(int id, String employeeId) throws SQLException {
        StockAdjustmentDAO.UserRef user = requireAdmin(employeeId);
        if (id <= 0) throw new IllegalArgumentException("Adjustment is missing.");
        dao.approve(id, user.id());
    }

    public void reject(int id, String employeeId, String note) throws SQLException {
        StockAdjustmentDAO.UserRef user = requireAdmin(employeeId);
        if (id <= 0) throw new IllegalArgumentException("Adjustment is missing.");
        if (note == null || note.isBlank()) throw new IllegalArgumentException("A rejection note is required.");
        dao.reject(id, user.id(), note.trim());
    }

    public void post(int id, String employeeId) throws SQLException {
        StockAdjustmentDAO.UserRef user = requireAdmin(employeeId);
        if (id <= 0) throw new IllegalArgumentException("Adjustment is missing.");
        dao.post(id, user.id());
    }

    private StockAdjustmentDAO.UserRef requireAdmin(String employeeId) throws SQLException {
        if (employeeId == null || employeeId.isBlank()) throw new IllegalArgumentException("Only a supervisor (Admin) can do this.");
        StockAdjustmentDAO.UserRef user = dao.findUser(employeeId);
        if (!"Admin".equalsIgnoreCase(user.role())) throw new IllegalArgumentException("Only a supervisor (Admin) can do this.");
        return user;
    }

    private String canonicalReason(String reason) {
        if (reason == null) throw new IllegalArgumentException("Reason must be one of: Count discrepancy, Damaged goods, Loss, Found stock.");
        String value = reason.trim();
        for (String candidate : REASONS) if (candidate.equalsIgnoreCase(value)) return candidate;
        throw new IllegalArgumentException("Reason must be one of: Count discrepancy, Damaged goods, Loss, Found stock.");
    }
}
