# Migration Guide: MySQL to SQL Server

## Overview

This guide provides step-by-step instructions to migrate the e-commerce microservices from MySQL to SQL Server.

## Prerequisites

✓ SQL Server 2019 or later installed and running
✓ SQL Server Management Studio (SSMS) installed
✓ All microservices stopped
✓ Backup of current MySQL databases

## Step 1: Prepare SQL Server Environment

### 1.1 Create Logins (if using SQL Authentication)

```sql
-- Execute as SA in SQL Server
CREATE LOGIN [ecommerce_user] WITH PASSWORD = 'YourPassword123';

-- Create user in each database and grant permissions
USE product_catalog;
CREATE USER [ecommerce_user] FOR LOGIN [ecommerce_user];
GRANT CONTROL ON DATABASE::product_catalog TO [ecommerce_user];

USE users;
CREATE USER [ecommerce_user] FOR LOGIN [ecommerce_user];
GRANT CONTROL ON DATABASE::users TO [ecommerce_user];

USE orders;
CREATE USER [ecommerce_user] FOR LOGIN [ecommerce_user];
GRANT CONTROL ON DATABASE::orders TO [ecommerce_user];
```

### 1.2 Execute Database Setup Scripts

Execute in SQL Server Management Studio in this order:
1. `01-product-catalog-db.sql`
2. `02-users-db.sql`
3. `03-orders-db.sql`

## Step 2: Migrate Existing Data (Optional)

If you have existing data in MySQL:

### 2.1 Export MySQL Data

```bash
# Export from MySQL
mysqldump -u root -p product_catalog > product_catalog_backup.sql
mysqldump -u root -p users > users_backup.sql
mysqldump -u root -p orders > orders_backup.sql
```

### 2.2 Import to SQL Server

Options:
1. **Use SQL Server Integration Services (SSIS)** - Recommended for large datasets
2. **Manual mapping** - Use SSMS import/export wizard
3. **Query-based** - Write INSERT scripts for each table

## Step 3: Update POM.xml

### Remove MySQL Dependency

Find and remove:
```xml
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.x.x</version>
</dependency>
```

### Add SQL Server Dependency

Add to all services' pom.xml:
```xml
<dependency>
    <groupId>com.microsoft.sqlserver</groupId>
    <artifactId>mssql-jdbc</artifactId>
    <version>12.2.0.jre11</version>
</dependency>
```

## Step 4: Update Configuration Files

### 4.1 Product Catalog Service

Replace `product-catalog-service/src/main/resources/application.properties`:

```properties
#Server port :
server.port=8810

#Application name :
spring.application.name=product-catalog-service

#Client registration properties :
eureka.client.service-url.defaultZone=http://localhost:8761/eureka

#Data source - SQL Server Configuration :
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=product_catalog;encrypt=true;trustServerCertificate=true
spring.datasource.username=sa
spring.datasource.password=YourPassword123
spring.datasource.driverClassName=com.microsoft.sqlserver.jdbc.SQLServerDriver

#Jpa/Hibernate :
spring.jpa.show-sql=true
spring.jpa.hibernate.dialect=org.hibernate.dialect.SQLServer2012Dialect
spring.jpa.hibernate.ddl-auto=validate

# Static resource configuration for uploaded images
spring.resources.static-locations=file:uploads/images/
spring.mvc.static-path-pattern=/images/**
```

### 4.2 User Service

Replace `user-service/src/main/resources/application.properties`:

```properties
##### Service details: #####
server.port=8811
spring.application.name=user-service
server.url=http://localhost:8811

#Client registration properties :
eureka.client.service-url.defaultZone=http://localhost:8761/eureka

#Data source - SQL Server Configuration :
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=users;encrypt=true;trustServerCertificate=true
spring.datasource.username=sa
spring.datasource.password=YourPassword123
spring.datasource.driverClassName=com.microsoft.sqlserver.jdbc.SQLServerDriver

#Jpa/Hibernate :
spring.jpa.show-sql=true
spring.jpa.hibernate.dialect=org.hibernate.dialect.SQLServer2012Dialect
spring.jpa.hibernate.ddl-auto=validate

##### HTTP #####
error.404.schema.details=The resource you are looking for might have been removed, had its name changed, or is temporarily unavailable
error.406.schema.details=This request is not acceptable. Supported MIME types are application/json
```

### 4.3 Order Service

Replace `order-service/src/main/resources/application.properties`:

```properties
#Server port :
server.port=8813

#Application name :
spring.application.name=order-service

#Client registration properties:
eureka.client.service-url.defaultZone=http://localhost:8761/eureka

#Data source - SQL Server Configuration :
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=orders;encrypt=true;trustServerCertificate=true
spring.datasource.username=sa
spring.datasource.password=YourPassword123
spring.datasource.driverClassName=com.microsoft.sqlserver.jdbc.SQLServerDriver

#Jpa/Hibernate :
spring.jpa.show-sql=true
spring.jpa.hibernate.dialect=org.hibernate.dialect.SQLServer2012Dialect
spring.jpa.hibernate.ddl-auto=validate

#Redis
spring.session.redis.namespace=session
spring.session.store-type=redis
spring.redis.host=localhost
spring.redis.port=6379
```

## Step 5: Update JPA/Hibernate Configuration

### Key Changes for SQL Server

1. **Dialect**: Changed from `MySQL8Dialect` to `SQLServer2012Dialect`
2. **DDL Strategy**: Set to `validate` (not `update` for production)
3. **JDBC URL**: Uses SQL Server format with encryption support

### Important Notes

- SQL Server uses `IDENTITY` for auto-increment instead of MySQL's `AUTO_INCREMENT`
- Timestamps use `DATETIME` instead of MySQL's `TIMESTAMP`
- Unicode support: Always use `NVARCHAR` instead of `VARCHAR`
- Use `DECIMAL(19,2)` for money values

## Step 6: Entity Annotations Compatibility

The existing JPA annotations are compatible with SQL Server:

```java
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Works with SQL Server IDENTITY
    private Long id;
    
    @Column(name = "product_name")
    private String productName;
    
    // ... other fields
}
```

## Step 7: Testing

### 7.1 Unit Tests

```bash
cd product-catalog-service
mvn test

cd ../user-service
mvn test

cd ../order-service
mvn test
```

### 7.2 Build Services

```bash
mvn clean package -DskipTests
```

### 7.3 Run Services

Start services in order:
1. Eureka Server (port 8761)
2. API Gateway (port 8765)
3. User Service (port 8811)
4. Product Catalog Service (port 8810)
5. Order Service (port 8813)

### 7.4 Smoke Tests

Test API endpoints:
```bash
# Test product service
curl http://localhost:8765/api/catalog/products

# Test user service
curl http://localhost:8765/api/accounts/users

# Test order service
curl http://localhost:8765/api/shop/orders
```

## Step 8: Verification

### Check SQL Server Databases

```sql
-- In SQL Server Management Studio
USE product_catalog;
SELECT COUNT(*) as product_count FROM products;
SELECT COUNT(*) as category_count FROM categories;

USE users;
SELECT COUNT(*) as user_count FROM users;

USE orders;
SELECT COUNT(*) as order_count FROM orders;
```

### Check Logs

Monitor Spring Boot logs for:
```
Dialect: org.hibernate.dialect.SQLServer2012Dialect
Driver: com.microsoft.sqlserver.jdbc.SQLServerDriver
```

## Rollback Plan

If issues occur:

### Option 1: Revert to MySQL
1. Restore MySQL from backup
2. Revert application.properties to MySQL configuration
3. Add MySQL JDBC driver back to pom.xml
4. Rebuild and restart services

### Option 2: Keep Both (Dual Setup)
Use Spring profiles:
```bash
# Start with MySQL
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=mysql"

# Start with SQL Server
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=sqlserver"
```

## Performance Tuning for SQL Server

### 1. Update Statistics

```sql
-- Run after data migration
EXEC sp_updatestats;
```

### 2. Index Optimization

```sql
-- Check fragmentation
SELECT object_id, avg_fragmentation_in_percent 
FROM sys.dm_db_index_physical_stats(DB_ID(), NULL, NULL, NULL, 'LIMITED')
WHERE avg_fragmentation_in_percent > 10;

-- Rebuild fragmented indexes
ALTER INDEX index_name ON table_name REBUILD;
```

### 3. Query Performance

```sql
-- Enable execution plans
SET STATISTICS IO ON;
SET STATISTICS TIME ON;
```

## Troubleshooting

### Connection Refused
```
Check: SQL Server is running on port 1433
Check: Firewall allows port 1433
Check: Database name is correct
```

### Login Failed
```
Check: Username and password are correct
Check: User has database permissions
Check: Login is not locked out
```

### Driver Not Found
```
Solution: Ensure mssql-jdbc-12.2.0.jre11.jar is in classpath
Check: mvn dependency:tree | grep mssql
```

### Hibernate Mapping Errors
```
Check: Entity annotations match SQL Server data types
Check: Table and column names in @Table and @Column
Check: Dialect is set to SQLServer2012Dialect
```

## Additional Resources

- [SQL Server JDBC Documentation](https://docs.microsoft.com/en-us/sql/connect/jdbc/getting-started-with-the-jdbc-driver)
- [Spring Boot SQL Server Integration](https://spring.io/guides/gs/accessing-data-jpa/)
- [Hibernate SQL Server Dialect](https://docs.jboss.org/hibernate/orm/5.6/javadocs/org/hibernate/dialect/SQLServer2012Dialect.html)
- [SQL Server Performance Tuning](https://docs.microsoft.com/en-us/sql/relational-databases/performance/monitoring-and-tuning-for-performance)

## Checklist

- [ ] SQL Server installed and running
- [ ] All 3 SQL scripts executed successfully
- [ ] pom.xml updated with SQL Server dependency
- [ ] application.properties files updated in all services
- [ ] Configuration tested with actual connections
- [ ] Services compile successfully
- [ ] Unit tests pass
- [ ] API endpoints respond correctly
- [ ] Data appears in SQL Server databases
- [ ] Monitoring and logging configured
