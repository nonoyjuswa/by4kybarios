USE myo_hardware;

CREATE TABLE IF NOT EXISTS stock_adjustments (
    adjustment_id INT PRIMARY KEY AUTO_INCREMENT,
    reference_no VARCHAR(20) NOT NULL UNIQUE,
    rack_id INT NOT NULL,
    reason ENUM('Count discrepancy','Damaged goods','Loss','Found stock') NOT NULL,
    explanation TEXT NOT NULL,
    status ENUM('Draft','Pending Approval','Approved','Posted','Rejected') NOT NULL DEFAULT 'Draft',
    counted_by INT NOT NULL,
    approved_by INT NULL,
    review_note TEXT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT current_timestamp(),
    approved_at TIMESTAMP NULL,
    posted_at TIMESTAMP NULL,
    CONSTRAINT fk_stock_adjustment_rack FOREIGN KEY (rack_id) REFERENCES racks(rack_id),
    CONSTRAINT fk_stock_adjustment_counted_by FOREIGN KEY (counted_by) REFERENCES users(user_id),
    CONSTRAINT fk_stock_adjustment_approved_by FOREIGN KEY (approved_by) REFERENCES users(user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE IF NOT EXISTS stock_adjustment_lines (
    line_id INT PRIMARY KEY AUTO_INCREMENT,
    adjustment_id INT NOT NOT NULL,
    product_id INT NOT NULL,
    system_qty INT NOT NULL,
    counted_qty INT NOT NULL,
    variance INT NOT NULL,
    UNIQUE KEY uq_adjustment_product (adjustment_id, product_id),
    CONSTRAINT chk_adjustment_counted_qty CHECK (counted_qty >= 0),
    CONSTRAINT fk_stock_adjustment_line_adjustment FOREIGN KEY (adjustment_id) REFERENCES stock_adjustments(adjustment_id),
    CONSTRAINT fk_stock_adjustment_line_product FOREIGN KEY (product_id) REFERENCES products(product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
