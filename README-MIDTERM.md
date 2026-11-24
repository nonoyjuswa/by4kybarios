# My0_Hardware Midterm Inventory Module

This version keeps the existing NetBeans Swing `.java` and `.form` GUI files. The existing dashboard layout is retained; the Inventory and Users sidebar buttons open CRUD dialogs, Stock In/Out buttons open transaction dialogs, Rack Map shows per-rack inventory, and Ledger displays movement history.

## Architecture

- UI: `MainFrame`, `InventoryManagerDialog`, `UserManagerDialog`, `StockMovementDialog`
- Service: `service/ProductService`, `service/UserService`, `service/StockMovementService`
- DAO: `dao/ProductDAO`, `dao/UserDAO`, `dao/RackDAO`, `dao/InventoryDAO`, `dao/StockMovementDAO`, `dao/DashboardDAO`
- Database pool: `DBConnection` using HikariCP

## CRUD and business rules

- Products can be created, read, updated, and deleted in Inventory Management.
- Product name and rack location are required; quantity and reorder level must be non-negative whole numbers.
- A product with inventory rows or stock movement history cannot be deleted, protecting parent/child records and transaction history.
- Users can be created, read, updated, and deleted. Leave Password blank when updating to keep the current password.
- Stock In increases the product total and the selected product/rack inventory row, and creates an `IN` movement record in the same transaction.
- Stock Out checks the selected rack's available quantity, decreases rack and product totals, and records an `OUT` movement in the same transaction. A failed operation rolls back.
- The one-to-many relationship is represented by one product having multiple `inventory` rows, one per rack. `inventory.product_id` references `products.product_id`; `inventory.rack_id` references `racks.rack_id`.

## Database connection

`DBConnection.java` is configured for `jdbc:mariadb://localhost:3306/myo_hardware`, user `root`, empty password. Change these values if your local MariaDB settings differ. HikariCP and MariaDB JDBC dependencies remain in `pom.xml`.

## Important schema assumptions

The uploaded project did not include a SQL dump, so the code uses columns already visible in your project/dashboard and screenshots:

- `products`: `product_id`, `product_name`, `rack_location`, `quantity`, `reorder_level`
- `racks`: `rack_id`, `rack_code`, `zone`, `rack_name`, `status`
- `inventory`: `inventory_id`, `product_id`, `rack_id`, `quantity`, `updated_at`
- `users`: `user_id`, `employee_id`, `username`, `password`, `f_name`, `role`
- `stock_movements`: `movement_id`, `product_id`, `rack_id`, `user_id`, `movement_type`, `quantity`, `reference_no`, `remarks`, `movement_date`

Before running, use these commands in MariaDB to confirm that the real schema matches:

```sql
USE myo_hardware;
DESCRIBE products;
DESCRIBE racks;
DESCRIBE inventory;
DESCRIBE users;
DESCRIBE stock_movements;
```

If `products` has any additional `NOT NULL` columns without defaults, the Product Add operation must also collect and insert those fields. The source project did not contain a schema export, so those extra fields cannot be safely inferred.

## Run

Open the `MyoStockManagement` folder as a Maven project in NetBeans, confirm Java 21 or later is selected, ensure MariaDB is running and the schema exists, then Clean and Build and Run. The project uses the existing login screen as its entry point.

**Security note:** the existing login design compares passwords as plain text because that is how the current database query is written. For a production system, password hashes should be stored and verified instead.
