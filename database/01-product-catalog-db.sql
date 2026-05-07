-- =====================================================
-- Product Catalog Database (SQL Server)
-- =====================================================

-- Create Database
IF NOT EXISTS (SELECT * FROM sys.databases WHERE name = 'product_catalog')
BEGIN
    CREATE DATABASE product_catalog;
END
GO

USE product_catalog;
GO

-- =====================================================
-- Categories Table
-- =====================================================
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'categories')
BEGIN
    CREATE TABLE categories (
        id BIGINT PRIMARY KEY IDENTITY(1,1),
        category_name NVARCHAR(255) NOT NULL UNIQUE,
        description NVARCHAR(MAX) NULL,
        created_at DATETIME DEFAULT GETDATE(),
        updated_at DATETIME DEFAULT GETDATE()
    );
END
GO

-- =====================================================
-- Products Table
-- =====================================================
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'products')
BEGIN
    CREATE TABLE products (
        id BIGINT PRIMARY KEY IDENTITY(1,1),
        product_name NVARCHAR(255) NOT NULL,
        price DECIMAL(19,2) NOT NULL,
        discription NVARCHAR(MAX) NOT NULL,
        image_url NVARCHAR(MAX) NOT NULL,
        category_id BIGINT NOT NULL,
        availability NVARCHAR(50) DEFAULT 'AVAILABLE',
        quantity INT DEFAULT 0,
        created_at DATETIME DEFAULT GETDATE(),
        updated_at DATETIME DEFAULT GETDATE(),
        CONSTRAINT fk_products_category FOREIGN KEY (category_id) REFERENCES categories(id)
    );
    
    CREATE INDEX idx_products_category ON products(category_id);
    CREATE INDEX idx_products_name ON products(product_name);
END
GO

-- =====================================================
-- Product Images Table
-- =====================================================
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'product_images')
BEGIN
    CREATE TABLE product_images (
        id BIGINT PRIMARY KEY IDENTITY(1,1),
        image_url NVARCHAR(MAX) NOT NULL,
        product_id BIGINT NOT NULL,
        created_at DATETIME DEFAULT GETDATE(),
        updated_at DATETIME DEFAULT GETDATE(),
        CONSTRAINT fk_product_images_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
    );
    
    CREATE INDEX idx_product_images_product ON product_images(product_id);
END
GO

-- =====================================================
-- Reviews Table
-- =====================================================
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'reviews')
BEGIN
    CREATE TABLE reviews (
        id BIGINT PRIMARY KEY IDENTITY(1,1),
        product_id BIGINT NOT NULL,
        user_id BIGINT NULL,
        rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
        comment NVARCHAR(MAX) NULL,
        created_at DATETIME DEFAULT GETDATE(),
        updated_at DATETIME DEFAULT GETDATE(),
        CONSTRAINT fk_reviews_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
    );
    
    CREATE INDEX idx_reviews_product ON reviews(product_id);
    CREATE INDEX idx_reviews_user ON reviews(user_id);
END
GO

-- =====================================================
-- Sample Data
-- =====================================================
IF NOT EXISTS (SELECT * FROM categories WHERE category_name = N'Electronics')
BEGIN
    INSERT INTO categories (category_name, description) VALUES 
        (N'Electronics', N'Electronic products'),
        (N'Books', N'Books and publications'),
        (N'Clothing', N'Apparel and fashion'),
        (N'Food & Beverages', N'Food and beverage products');
END
GO

-- =====================================================
-- Stored Procedures
-- =====================================================

-- Get products by category
IF EXISTS (SELECT * FROM sys.objects WHERE type = 'P' AND name = 'sp_GetProductsByCategory')
    DROP PROCEDURE sp_GetProductsByCategory;
GO

CREATE PROCEDURE sp_GetProductsByCategory
    @CategoryId BIGINT
AS
BEGIN
    SELECT p.* FROM products p 
    WHERE p.category_id = @CategoryId 
    ORDER BY p.created_at DESC;
END
GO

-- Get product with all images
IF EXISTS (SELECT * FROM sys.objects WHERE type = 'P' AND name = 'sp_GetProductWithImages')
    DROP PROCEDURE sp_GetProductWithImages;
GO

CREATE PROCEDURE sp_GetProductWithImages
    @ProductId BIGINT
AS
BEGIN
    SELECT p.*, 
           (SELECT COUNT(*) FROM product_images WHERE product_id = p.id) as image_count
    FROM products p
    WHERE p.id = @ProductId;
    
    SELECT * FROM product_images WHERE product_id = @ProductId;
END
GO

PRINT 'Product Catalog Database setup completed successfully!';
