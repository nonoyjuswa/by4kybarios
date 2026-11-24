USE myo_hardware;
DESCRIBE products;
DESCRIBE racks;
DESCRIBE inventory;
DESCRIBE users;
DESCRIBE stock_movements;

-- Verify that product/rack child records are linked as expected.
SELECT p.product_id, p.product_name, i.inventory_id, r.rack_code, i.quantity
FROM products p
LEFT JOIN inventory i ON i.product_id = p.product_id
LEFT JOIN racks r ON r.rack_id = i.rack_id
ORDER BY p.product_id, r.rack_code;

-- Verify recent stock movement history.
SELECT sm.movement_id, sm.movement_type, p.product_name, r.rack_code,
       sm.quantity, u.employee_id, sm.movement_date
FROM stock_movements sm
JOIN products p ON p.product_id = sm.product_id
JOIN racks r ON r.rack_id = sm.rack_id
JOIN users u ON u.user_id = sm.user_id
ORDER BY sm.movement_date DESC
LIMIT 20;
