package com.mycompany.myostockmanagement.model;

public record AdjustmentLine(int productId, String productName, int systemQty, int countedQty) {
    public int variance() {
        return countedQty - systemQty;
    }
}
