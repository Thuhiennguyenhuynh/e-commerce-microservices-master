-- =====================================================
-- Users Database (SQL Server)
-- =====================================================

-- Create Database
IF NOT EXISTS (SELECT * FROM sys.databases WHERE name = 'users')
BEGIN
    CREATE DATABASE users;
END
GO

USE users;
GO

-- =====================================================
-- User Roles Table
-- =====================================================
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'user_roles')
BEGIN
    CREATE TABLE user_roles (
        id BIGINT PRIMARY KEY IDENTITY(1,1),
        role_name NVARCHAR(50) NOT NULL UNIQUE,
        description NVARCHAR(255) NULL,
        created_at DATETIME DEFAULT GETDATE(),
        updated_at DATETIME DEFAULT GETDATE()
    );
END
GO

-- =====================================================
-- Users Details Table
-- =====================================================
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'users_details')
BEGIN
    CREATE TABLE users_details (
        id BIGINT PRIMARY KEY IDENTITY(1,1),
        first_name NVARCHAR(50) NOT NULL,
        last_name NVARCHAR(50) NOT NULL,
        email NVARCHAR(100) NOT NULL UNIQUE,
        phone_number NVARCHAR(15) NULL,
        street NVARCHAR(100) NULL,
        street_number NVARCHAR(10) NULL,
        zip_code NVARCHAR(10) NULL,
        locality NVARCHAR(100) NULL,
        country NVARCHAR(100) NULL,
        image_url NVARCHAR(MAX) NULL,
        created_at DATETIME DEFAULT GETDATE(),
        updated_at DATETIME DEFAULT GETDATE()
    );
    
    CREATE INDEX idx_users_details_email ON users_details(email);
END
GO

-- =====================================================
-- Users Table
-- =====================================================
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'users')
BEGIN
    CREATE TABLE users (
        id BIGINT PRIMARY KEY IDENTITY(1,1),
        user_name NVARCHAR(50) NOT NULL UNIQUE,
        user_password NVARCHAR(255) NOT NULL,
        active INT DEFAULT 1,
        user_details_id BIGINT NULL UNIQUE,
        role_id BIGINT NOT NULL,
        created_at DATETIME DEFAULT GETDATE(),
        updated_at DATETIME DEFAULT GETDATE(),
        CONSTRAINT fk_users_user_details FOREIGN KEY (user_details_id) REFERENCES users_details(id) ON DELETE CASCADE,
        CONSTRAINT fk_users_role FOREIGN KEY (role_id) REFERENCES user_roles(id)
    );
    
    CREATE INDEX idx_users_username ON users(user_name);
    CREATE INDEX idx_users_active ON users(active);
    CREATE INDEX idx_users_role ON users(role_id);
END
GO

-- =====================================================
-- Sample Data
-- =====================================================
IF NOT EXISTS (SELECT * FROM user_roles WHERE role_name = N'ADMIN')
BEGIN
    INSERT INTO user_roles (role_name, description) VALUES 
        (N'ADMIN', N'Administrator'),
        (N'USER', N'Regular User'),
        (N'SELLER', N'Seller');
END
GO

-- =====================================================
-- Stored Procedures
-- =====================================================

-- Get user by username
IF EXISTS (SELECT * FROM sys.objects WHERE type = 'P' AND name = 'sp_GetUserByUsername')
    DROP PROCEDURE sp_GetUserByUsername;
GO

CREATE PROCEDURE sp_GetUserByUsername
    @UserName NVARCHAR(50)
AS
BEGIN
    SELECT u.*, ud.*, ur.role_name
    FROM users u
    LEFT JOIN users_details ud ON u.user_details_id = ud.id
    LEFT JOIN user_roles ur ON u.role_id = ur.id
    WHERE u.user_name = @UserName;
END
GO

-- Get user by email
IF EXISTS (SELECT * FROM sys.objects WHERE type = 'P' AND name = 'sp_GetUserByEmail')
    DROP PROCEDURE sp_GetUserByEmail;
GO

CREATE PROCEDURE sp_GetUserByEmail
    @Email NVARCHAR(100)
AS
BEGIN
    SELECT u.*, ud.*, ur.role_name
    FROM users u
    LEFT JOIN users_details ud ON u.user_details_id = ud.id
    LEFT JOIN user_roles ur ON u.role_id = ur.id
    WHERE ud.email = @Email;
END
GO

-- Register new user
IF EXISTS (SELECT * FROM sys.objects WHERE type = 'P' AND name = 'sp_RegisterUser')
    DROP PROCEDURE sp_RegisterUser;
GO

CREATE PROCEDURE sp_RegisterUser
    @UserName NVARCHAR(50),
    @UserPassword NVARCHAR(255),
    @FirstName NVARCHAR(50),
    @LastName NVARCHAR(50),
    @Email NVARCHAR(100),
    @PhoneNumber NVARCHAR(15) = NULL,
    @RoleId BIGINT
AS
BEGIN
    BEGIN TRANSACTION;
    
    BEGIN TRY
        -- Check if username exists
        IF EXISTS (SELECT 1 FROM users WHERE user_name = @UserName)
            THROW 50001, 'Username already exists', 1;
        
        -- Check if email exists
        IF EXISTS (SELECT 1 FROM users_details WHERE email = @Email)
            THROW 50002, 'Email already exists', 1;
        
        -- Create user details
        INSERT INTO users_details (first_name, last_name, email, phone_number)
        VALUES (@FirstName, @LastName, @Email, @PhoneNumber);
        
        DECLARE @UserDetailsId BIGINT = SCOPE_IDENTITY();
        
        -- Create user
        INSERT INTO users (user_name, user_password, active, user_details_id, role_id)
        VALUES (@UserName, @UserPassword, 1, @UserDetailsId, @RoleId);
        
        COMMIT TRANSACTION;
        SELECT 'User registered successfully' as message;
    END TRY
    BEGIN CATCH
        ROLLBACK TRANSACTION;
        THROW;
    END CATCH
END
GO

PRINT 'Users Database setup completed successfully!';
