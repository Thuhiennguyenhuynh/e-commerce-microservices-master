# SQL Server Database Setup Guide

## Prerequisites
- SQL Server 2019 or later installed
- SQL Server Management Studio (SSMS)
- Microsoft JDBC Driver for SQL Server 12.2 or later

## Setup Steps

### 1. Execute Database Setup Scripts

Run the SQL scripts in order using SQL Server Management Studio:

1. **01-product-catalog-db.sql** - Creates product_catalog database
   - Creates: categories, products, product_images, reviews tables
   - Includes sample data and stored procedures

2. **02-users-db.sql** - Creates users database  
   - Creates: user_roles, users_details, users tables
   - Includes stored procedures for user management

3. **03-orders-db.sql** - Creates orders database
   - Creates: inventory, items, orders, cart tables
   - Includes stored procedures for order and inventory management

### 2. Update Application Properties

Update the datasource configuration in each service's `application.properties`:

**For SQL Server with Windows Authentication:**
```properties
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=DATABASE_NAME;encrypt=true;trustServerCertificate=true
spring.datasource.username=
spring.datasource.password=
spring.datasource.driverClassName=com.microsoft.sqlserver.jdbc.SQLServerDriver
spring.jpa.hibernate.dialect=org.hibernate.dialect.SQLServer2012Dialect
```

**For SQL Server with SQL Authentication:**
```properties
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=DATABASE_NAME;encrypt=true;trustServerCertificate=true
spring.datasource.username=sa
spring.datasource.password=YourPassword
spring.datasource.driverClassName=com.microsoft.sqlserver.jdbc.SQLServerDriver
spring.jpa.hibernate.dialect=org.hibernate.dialect.SQLServer2012Dialect
```

### 3. Add SQL Server JDBC Dependency

Add to `pom.xml`:
```xml
<dependency>
    <groupId>com.microsoft.sqlserver</groupId>
    <artifactId>mssql-jdbc</artifactId>
    <version>12.2.0.jre11</version>
</dependency>
```

### 4. Database Connections

| Service | Database | URL | Port |
|---------|----------|-----|------|
| product-catalog-service | product_catalog | localhost | 1433 |
| user-service | users | localhost | 1433 |
| order-service | orders | localhost | 1433 |

## Service Configuration

### Product Catalog Service (Port 8810)
```properties
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=product_catalog;encrypt=true;trustServerCertificate=true
spring.jpa.hibernate.ddl-auto=validate
```

### User Service (Port 8811)
```properties
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=users;encrypt=true;trustServerCertificate=true
spring.jpa.hibernate.ddl-auto=validate
```

### Order Service (Port 8813)
```properties
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=orders;encrypt=true;trustServerCertificate=true
spring.jpa.hibernate.ddl-auto=validate
```

## Key Features

### Product Catalog Database
- Products with categories
- Product images (one-to-many relationship)
- Reviews and ratings
- Stored procedures for efficient queries

### Users Database  
- User registration and authentication
- User roles (ADMIN, USER, SELLER)
- User details (address, contact info)
- Stored procedure for user registration with transaction

### Orders Database
- Order management with status tracking
- Order items with pricing
- Inventory tracking
- Payment status monitoring
- Shopping cart (join table)
- Stored procedures for order creation and inventory management

## Stored Procedures Available

### Product Catalog
- `sp_GetProductsByCategory` - Get products by category
- `sp_GetProductWithImages` - Get product with all images

### Users
- `sp_GetUserByUsername` - Find user by username
- `sp_GetUserByEmail` - Find user by email  
- `sp_RegisterUser` - Register new user with transaction

### Orders
- `sp_GetOrderWithItems` - Get order details with items
- `sp_GetUserOrders` - Get all orders for a user
- `sp_CreateOrder` - Create new order
- `sp_UpdateOrderStatus` - Update order status
- `sp_GetInventoryStatus` - Check inventory levels
- `sp_ReserveStock` - Reserve stock for order (with transaction)
- `sp_ReleaseStock` - Release stock (rollback)

## Data Integrity

- **Constraints**: NOT NULL, UNIQUE, CHECK, FOREIGN KEY
- **Transactions**: Used in critical operations (user registration, stock operations)
- **Indexes**: Created on frequently queried columns for performance
- **Cascade Delete**: Configured on related tables for data consistency

## Performance Considerations

- Indexes on: category_id, product_name, email, username, order status
- Stored procedures for complex operations
- Appropriate data types (NVARCHAR for Unicode support)
- Default values for audit columns (created_at, updated_at)

## Troubleshooting

### Connection Issues
- Verify SQL Server is running: `sqlcmd -S localhost -U sa -P <password>`
- Check firewall allows port 1433
- Verify JDBC driver version compatibility

### Migration Issues
- Keep `spring.jpa.hibernate.ddl-auto=validate` after initial setup
- Use Flyway or Liquibase for production migrations
- Backup database before major schema changes

## References
- [SQL Server Documentation](https://docs.microsoft.com/en-us/sql/sql-server/)
- [Microsoft JDBC Driver](https://github.com/microsoft/mssql-jdbc)
- [Spring Boot & SQL Server](https://spring.io/guides/gs/accessing-data-jpa/)
