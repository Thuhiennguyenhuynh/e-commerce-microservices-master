-- =====================================================
-- Orders Database (SQL Server)
-- =====================================================

-- Create Database
IF NOT EXISTS (SELECT * FROM sys.databases WHERE name = 'orders')
BEGIN
    CREATE DATABASE orders;
END
GO

USE orders;
GO

-- =====================================================
-- Products Table (Reference only - normally from product-catalog-service)
-- =====================================================
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'products')
BEGIN
    CREATE TABLE products (
        id BIGINT PRIMARY KEY,
        product_name NVARCHAR(255) NOT NULL,
        price DECIMAL(19,2) NOT NULL,
        quantity INT DEFAULT 0,
        created_at DATETIME DEFAULT GETDATE(),
        updated_at DATETIME DEFAULT GETDATE()
    );
END
GO

-- =====================================================
-- Users Table (Reference only - normally from user-service)
-- =====================================================
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'users')
BEGIN
    CREATE TABLE users (
        id BIGINT PRIMARY KEY,
        user_name NVARCHAR(50) NOT NULL,
        created_at DATETIME DEFAULT GETDATE(),
        updated_at DATETIME DEFAULT GETDATE()
    );
END
GO

-- =====================================================
-- Inventory Table
-- =====================================================
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'inventory')
BEGIN
    CREATE TABLE inventory (
        id BIGINT PRIMARY KEY IDENTITY(1,1),
        ingredient_name NVARCHAR(255) NOT NULL,
        unit NVARCHAR(50) NOT NULL,
        opening_stock INT NOT NULL DEFAULT 0,
        stock_in INT NOT NULL DEFAULT 0,
        closing_stock INT NOT NULL DEFAULT 0,
        created_at DATETIME DEFAULT GETDATE(),
        updated_at DATETIME DEFAULT GETDATE(),
        CHECK (opening_stock >= 0),
        CHECK (stock_in >= 0),
        CHECK (closing_stock >= 0)
    );
    
    CREATE INDEX idx_inventory_ingredient ON inventory(ingredient_name);
END
GO

-- =====================================================
-- Items Table (Order Items)
-- =====================================================
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'items')
BEGIN
    CREATE TABLE items (
        id BIGINT PRIMARY KEY IDENTITY(1,1),
        quantity INT NOT NULL CHECK (quantity > 0),
        subtotal DECIMAL(19,2) NOT NULL CHECK (subtotal > 0),
        product_id BIGINT NOT NULL,
        created_at DATETIME DEFAULT GETDATE(),
        updated_at DATETIME DEFAULT GETDATE(),
        CONSTRAINT fk_items_product FOREIGN KEY (product_id) REFERENCES products(id)
    );
    
    CREATE INDEX idx_items_product ON items(product_id);
END
GO

-- =====================================================
-- Orders Table
-- =====================================================
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'orders')
BEGIN
    CREATE TABLE orders (
        id BIGINT PRIMARY KEY IDENTITY(1,1),
        ordered_date DATE NOT NULL DEFAULT CAST(GETDATE() AS DATE),
        status NVARCHAR(50) NOT NULL DEFAULT 'PENDING',
        total DECIMAL(19,2) NOT NULL,
        user_id BIGINT NOT NULL,
        payment_method NVARCHAR(50) NULL,
        payment_status NVARCHAR(50) DEFAULT 'PENDING',
        shipping_address NVARCHAR(MAX) NULL,
        notes NVARCHAR(MAX) NULL,
        created_at DATETIME DEFAULT GETDATE(),
        updated_at DATETIME DEFAULT GETDATE(),
        CONSTRAINT fk_orders_user FOREIGN KEY (user_id) REFERENCES users(id),
        CHECK (total > 0),
        CHECK (status IN ('PENDING', 'AWAITING_PAYMENT', 'PAID', 'PROCESSING', 'SHIPPED', 'DELIVERED', 'CANCELLED', 'FAILED'))
    );
    
    CREATE INDEX idx_orders_user ON orders(user_id);
    CREATE INDEX idx_orders_status ON orders(status);
    CREATE INDEX idx_orders_date ON orders(ordered_date);
END
GO

-- =====================================================
-- Cart (Order-Item Join Table)
-- =====================================================
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'cart')
BEGIN
    CREATE TABLE cart (
        order_id BIGINT NOT NULL,
        item_id BIGINT NOT NULL,
        created_at DATETIME DEFAULT GETDATE(),
        updated_at DATETIME DEFAULT GETDATE(),
        PRIMARY KEY (order_id, item_id),
        CONSTRAINT fk_cart_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
        CONSTRAINT fk_cart_item FOREIGN KEY (item_id) REFERENCES items(id) ON DELETE CASCADE
    );
    
    CREATE INDEX idx_cart_order ON cart(order_id);
    CREATE INDEX idx_cart_item ON cart(item_id);
END
GO

-- =====================================================
-- Sample Data
-- =====================================================
IF NOT EXISTS (SELECT * FROM inventory WHERE ingredient_name = N'Sample Ingredient')
BEGIN
    INSERT INTO inventory (ingredient_name, unit, opening_stock, stock_in, closing_stock)
    VALUES (N'Sample Ingredient', N'kg', 100, 50, 150);
END
GO

-- =====================================================
-- Stored Procedures
-- =====================================================

-- Get order by ID with items
IF EXISTS (SELECT * FROM sys.objects WHERE type = 'P' AND name = 'sp_GetOrderWithItems')
    DROP PROCEDURE sp_GetOrderWithItems;
GO

CREATE PROCEDURE sp_GetOrderWithItems
    @OrderId BIGINT
AS
BEGIN
    -- Get order details
    SELECT * FROM orders WHERE id = @OrderId;
    
    -- Get items in order
    SELECT i.* FROM items i
    INNER JOIN cart c ON i.id = c.item_id
    WHERE c.order_id = @OrderId;
END
GO

-- Get user orders
IF EXISTS (SELECT * FROM sys.objects WHERE type = 'P' AND name = 'sp_GetUserOrders')
    DROP PROCEDURE sp_GetUserOrders;
GO

CREATE PROCEDURE sp_GetUserOrders
    @UserId BIGINT,
    @Status NVARCHAR(50) = NULL
AS
BEGIN
    IF @Status IS NULL
        SELECT * FROM orders WHERE user_id = @UserId ORDER BY ordered_date DESC;
    ELSE
        SELECT * FROM orders WHERE user_id = @UserId AND status = @Status ORDER BY ordered_date DESC;
END
GO

-- Create order with items
IF EXISTS (SELECT * FROM sys.objects WHERE type = 'P' AND name = 'sp_CreateOrder')
    DROP PROCEDURE sp_CreateOrder;
GO

CREATE PROCEDURE sp_CreateOrder
    @UserId BIGINT,
    @OrderedDate DATE,
    @Total DECIMAL(19,2),
    @Status NVARCHAR(50) = 'PENDING',
    @ShippingAddress NVARCHAR(MAX) = NULL
AS
BEGIN
    BEGIN TRANSACTION;
    
    BEGIN TRY
        -- Create order
        INSERT INTO orders (user_id, ordered_date, total, status, shipping_address)
        VALUES (@UserId, @OrderedDate, @Total, @Status, @ShippingAddress);
        
        DECLARE @OrderId BIGINT = SCOPE_IDENTITY();
        
        COMMIT TRANSACTION;
        SELECT @OrderId as order_id, 'Order created successfully' as message;
    END TRY
    BEGIN CATCH
        ROLLBACK TRANSACTION;
        THROW;
    END CATCH
END
GO

-- Update order status
IF EXISTS (SELECT * FROM sys.objects WHERE type = 'P' AND name = 'sp_UpdateOrderStatus')
    DROP PROCEDURE sp_UpdateOrderStatus;
GO

CREATE PROCEDURE sp_UpdateOrderStatus
    @OrderId BIGINT,
    @Status NVARCHAR(50),
    @PaymentStatus NVARCHAR(50) = NULL
AS
BEGIN
    UPDATE orders 
    SET status = @Status,
        payment_status = COALESCE(@PaymentStatus, payment_status),
        updated_at = GETDATE()
    WHERE id = @OrderId;
    
    IF @@ROWCOUNT = 0
        THROW 50001, 'Order not found', 1;
END
GO

-- Get inventory by product
IF EXISTS (SELECT * FROM sys.objects WHERE type = 'P' AND name = 'sp_GetInventoryStatus')
    DROP PROCEDURE sp_GetInventoryStatus;
GO

CREATE PROCEDURE sp_GetInventoryStatus
AS
BEGIN
    SELECT * FROM inventory ORDER BY updated_at DESC;
END
GO

-- Reserve stock
IF EXISTS (SELECT * FROM sys.objects WHERE type = 'P' AND name = 'sp_ReserveStock')
    DROP PROCEDURE sp_ReserveStock;
GO

CREATE PROCEDURE sp_ReserveStock
    @InventoryId BIGINT,
    @Quantity INT
AS
BEGIN
    BEGIN TRANSACTION;
    
    BEGIN TRY
        -- Check if enough stock
        DECLARE @AvailableStock INT = (SELECT closing_stock FROM inventory WHERE id = @InventoryId);
        
        IF @AvailableStock IS NULL
            THROW 50001, 'Inventory item not found', 1;
        
        IF @AvailableStock < @Quantity
            THROW 50002, 'Insufficient stock', 1;
        
        -- Update inventory
        UPDATE inventory 
        SET closing_stock = closing_stock - @Quantity,
            updated_at = GETDATE()
        WHERE id = @InventoryId;
        
        COMMIT TRANSACTION;
        SELECT 'Stock reserved successfully' as message;
    END TRY
    BEGIN CATCH
        ROLLBACK TRANSACTION;
        THROW;
    END CATCH
END
GO

-- Release stock (rollback reservation)
IF EXISTS (SELECT * FROM sys.objects WHERE type = 'P' AND name = 'sp_ReleaseStock')
    DROP PROCEDURE sp_ReleaseStock;
GO

CREATE PROCEDURE sp_ReleaseStock
    @InventoryId BIGINT,
    @Quantity INT
AS
BEGIN
    BEGIN TRANSACTION;
    
    BEGIN TRY
        UPDATE inventory 
        SET closing_stock = closing_stock + @Quantity,
            updated_at = GETDATE()
        WHERE id = @InventoryId;
        
        IF @@ROWCOUNT = 0
            THROW 50001, 'Inventory item not found', 1;
        
        COMMIT TRANSACTION;
        SELECT 'Stock released successfully' as message;
    END TRY
    BEGIN CATCH
        ROLLBACK TRANSACTION;
        THROW;
    END CATCH
END
GO

PRINT 'Orders Database setup completed successfully!';
