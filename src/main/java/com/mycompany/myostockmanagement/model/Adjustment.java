package com.mycompany.myostockmanagement.model;

import java.util.List;

public record Adjustment(int id, String referenceNo, String rackCode, String reason,
                         String explanation, String status, int countedById,
                         String countedBy, String approvedBy, String reviewNote,
                         List<AdjustmentLine> lines) {}
