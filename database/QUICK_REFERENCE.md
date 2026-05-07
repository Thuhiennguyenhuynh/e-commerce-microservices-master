# 🚀 Quick Reference Guide

## 📍 System Architecture at a Glance

```
Client (Vue.js:3000) 
    ↓ HTTP
API Gateway (8765)
    ├─ /api/accounts → User Service (8811)
    ├─ /api/catalog → Product Service (8810)
    └─ /api/shop → Order Service (8813)
         ↓
SQL Server (localhost:1433)
    ├─ Database: users
    ├─ Database: product_catalog
    └─ Database: orders
```

---

## 🗄️ Quick Table Reference

### Database 1: users
| Table | Columns | Primary Key | Key Relationships |
|-------|---------|------------|-------------------|
| user_roles | id, role_name, description | id | 1:N → users |
| users_details | id, first_name, last_name, email, phone, address fields, image_url | id | 1:1 ← users |
| users | id, user_name, user_password, active, user_details_id, role_id | id | N:1 → user_roles, 1:1 → users_details |

### Database 2: product_catalog
| Table | Columns | Primary Key | Key Relationships |
|-------|---------|------------|-------------------|
| categories | id, category_name, description | id | 1:N → products |
| products | id, product_name, price, description, image_url, category_id, availability, quantity | id | 1:N → product_images, reviews; N:1 ← categories |
| product_images | id, image_url, product_id | id | N:1 → products |
| reviews | id, product_id, user_id, rating, comment | id | N:1 → products |

### Database 3: orders
| Table | Columns | Primary Key | Key Relationships |
|-------|---------|------------|-------------------|
| products (ref) | id, product_name, price, quantity | id | 1:N → items |
| users (ref) | id, user_name | id | 1:N → orders |
| inventory | id, ingredient_name, unit, opening_stock, stock_in, closing_stock | id | - |
| items | id, quantity, subtotal, product_id | id | N:N ← orders (via cart) |
| orders | id, ordered_date, status, total, user_id, payment_method, payment_status | id | N:1 → users, 1:N → items (via cart) |
| cart | order_id, item_id | (order_id, item_id) | N:N junction |

---

## 🔄 Critical Data Flows

### 1️⃣ User Registration
```
POST /api/accounts/register
└─ UserService: Create users_details + users with role=USER
└─ Return: user_id, username
```

### 2️⃣ Product Listing
```
GET /api/catalog/products?categoryId=X
└─ ProductService: Query products by category
└─ Fetch product_images for each
└─ Return: [{id, name, price, imageUrl, images:[...]}]
```

### 3️⃣ Create Order
```
POST /api/shop/orders
└─ OrderService:
   1. Validate user (call user-service)
   2. Validate products (call product-service)
   3. Create Items for each product
   4. Reserve inventory
   5. Create Order with status=PENDING
   6. Link items via cart table
└─ Return: orderId, total, status
```

### 4️⃣ Payment Processing
```
POST /api/shop/orders/{orderId}/payment
└─ OrderService:
   1. Call Payment Gateway
   2. IF SUCCESS: Update order.status=PAID, Update inventory
   3. IF FAILED: Update order.status=FAILED, Release inventory
└─ Return: payment status
```

### 5️⃣ Upload Product Images
```
POST /api/catalog/admin/upload
└─ FileUploadController: Save file to uploads/images/
└─ Return: /images/{filename}

POST /api/catalog/admin/products
└─ ProductService:
   1. Create Product with imageUrl (main image)
   2. Create ProductImage records for extras
   3. Link via product.images relationship
└─ Return: productId
```

---

## 📊 Order Status Lifecycle

```
PENDING ──→ AWAITING_PAYMENT ──→ PAID ──→ PROCESSING ──→ SHIPPED ──→ DELIVERED
              ↓                                   ↑
           FAILED ──────────────────→ [Retry or Cancel]

ANY STATUS → CANCELLED (User initiates)
```

**Status Meanings**:
- **PENDING**: Order created, waiting for payment initiation
- **AWAITING_PAYMENT**: Payment page shown to user
- **PAID**: Payment successful, inventory committed
- **PROCESSING**: Preparing order for shipment
- **SHIPPED**: Order in transit
- **DELIVERED**: Order received by customer
- **FAILED**: Payment declined
- **CANCELLED**: User or admin cancelled order

---

## 🔐 Stored Procedures Cheat Sheet

### Product Catalog (product_catalog DB)
```sql
-- Get products by category
EXEC sp_GetProductsByCategory @CategoryId = 1

-- Get product with all images
EXEC sp_GetProductWithImages @ProductId = 100
```

### Users (users DB)
```sql
-- Find user by username
EXEC sp_GetUserByUsername @UserName = 'john_doe'

-- Find user by email
EXEC sp_GetUserByEmail @Email = 'john@example.com'

-- Register new user (with transaction)
EXEC sp_RegisterUser 
    @UserName = 'john_doe',
    @UserPassword = 'hashed_password',
    @FirstName = 'John',
    @LastName = 'Doe',
    @Email = 'john@example.com',
    @PhoneNumber = '123456789',
    @RoleId = 2  -- USER role
```

### Orders (orders DB)
```sql
-- Get order with items
EXEC sp_GetOrderWithItems @OrderId = 1000

-- Get user orders
EXEC sp_GetUserOrders @UserId = 5, @Status = 'PENDING'

-- Create order (with transaction)
EXEC sp_CreateOrder 
    @UserId = 5,
    @OrderedDate = CAST(GETDATE() AS DATE),
    @Total = 1500.00,
    @Status = 'PENDING'

-- Update order status
EXEC sp_UpdateOrderStatus 
    @OrderId = 1000,
    @Status = 'PAID',
    @PaymentStatus = 'PAID'

-- Reserve stock
EXEC sp_ReserveStock @InventoryId = 10, @Quantity = 5

-- Release stock (rollback)
EXEC sp_ReleaseStock @InventoryId = 10, @Quantity = 5
```

---

## 🌐 API Endpoints Mapping

### User Service (8811) → Exposed via /api/accounts
| Method | Endpoint | DB Query |
|--------|----------|----------|
| POST | /register | INSERT users, users_details |
| POST | /login | SELECT users WHERE username |
| GET | /users/{id} | SELECT users WHERE id |
| PUT | /users/{id} | UPDATE users SET ... |
| GET | /users/email/{email} | SELECT users WHERE email |

### Product Service (8810) → Exposed via /api/catalog
| Method | Endpoint | DB Query |
|--------|----------|----------|
| GET | /categories | SELECT categories |
| GET | /products | SELECT products with images |
| GET | /products/{id} | sp_GetProductWithImages |
| GET | /products?category={id} | sp_GetProductsByCategory |
| POST | /admin/products | INSERT products, product_images |
| POST | /admin/upload | Save file, return URL |
| GET | /reviews/{productId} | SELECT reviews |
| POST | /reviews | INSERT reviews |

### Order Service (8813) → Exposed via /api/shop
| Method | Endpoint | DB Query |
|--------|----------|----------|
| POST | /orders | INSERT orders, items, cart |
| GET | /orders/{id} | sp_GetOrderWithItems |
| GET | /orders | sp_GetUserOrders |
| POST | /orders/{id}/payment | UPDATE orders + inventory |
| PUT | /orders/{id}/status | sp_UpdateOrderStatus |
| GET | /inventory | SELECT inventory |

---

## 💾 Data Type Standards

| Concept | SQL Type | Size |
|---------|----------|------|
| IDs | BIGINT IDENTITY | 64-bit auto-increment |
| Money | DECIMAL(19,2) | Up to 17 digits + 2 decimals |
| Names | NVARCHAR(50-255) | Unicode, variable length |
| Text | NVARCHAR(MAX) | Up to 2GB |
| Dates | DATETIME | With time |
| Dates Only | DATE | Date only |
| Booleans | INT (0/1) | INT alternative |
| Flags | NVARCHAR(50) | Status: PENDING, PAID, etc |

---

## ✅ Validation Rules

| Field | Rule | Error Message |
|-------|------|---------------|
| product.price | > 0 | "Giá phải lớn hơn 0" |
| product.quantity | >= 0 | "Tồn kho không được âm" |
| reviews.rating | 1-5 | "Đánh giá phải từ 1-5 sao" |
| users.user_name | NOT NULL, UNIQUE | "Username đã tồn tại" |
| users_details.email | NOT NULL, UNIQUE | "Email đã tồn tại" |
| orders.total | > 0 | "Tổng tiền phải > 0" |
| orders.status | IN list | "Trạng thái không hợp lệ" |
| items.quantity | > 0 | "Số lượng phải > 0" |

---

## 🔧 Troubleshooting Checklist

### Connection Issues
- [ ] SQL Server running on 1433?
- [ ] Firewall allows 1433?
- [ ] Driver in classpath (mssql-jdbc)?
- [ ] Correct database name in URL?

### Data Issues
- [ ] Foreign keys valid?
- [ ] Constraints violated?
- [ ] Unique constraints duplicate?
- [ ] Not null fields empty?

### Performance Issues
- [ ] Indexes created?
- [ ] Queries using indexes?
- [ ] N+1 query problem?
- [ ] Large result sets?

### Application Issues
- [ ] Hibernate dialect correct (SQLServer2012Dialect)?
- [ ] DDL mode set to 'validate' after setup?
- [ ] Services can reach SQL Server?
- [ ] API Gateway routes correct?

---

## 📈 Key Metrics to Monitor

| Metric | What to Watch | Acceptable Range |
|--------|---------------|------------------|
| Response Time | API latency | < 500ms |
| Database Connections | Pool usage | < 80% |
| Order Success Rate | Payment completion | > 95% |
| Inventory Accuracy | Stock vs actual | 100% |
| Error Rate | Failed requests | < 1% |
| Concurrent Users | Peak load | TBD |

---

## 🔄 Transaction Scope

### ACID Guaranteed
```
✓ User Registration
✓ Order Creation
✓ Payment Processing
✓ Stock Reservation/Release
```

### Eventual Consistency
```
~ Image upload (best effort)
~ Review creation (async possible)
~ Cache invalidation (eventual)
```

---

## 🎯 Common Commands

### PowerShell (Backend)
```bash
# Build all services
mvn clean package -DskipTests

# Run single service
cd product-catalog-service
mvn spring-boot:run

# Check compile
mvn -q compile
```

### SQL Server (SSMS)
```sql
-- Connect
sqlcmd -S localhost -U sa -P YourPassword

-- Check databases
SELECT name FROM sys.databases;

-- Check table count
SELECT COUNT(*) FROM information_schema.tables;

-- Monitor connections
SELECT * FROM sys.dm_exec_sessions;
```

### cURL (API Testing)
```bash
# Get products
curl http://localhost:8765/api/catalog/products

# Create order
curl -X POST http://localhost:8765/api/shop/orders \
  -H "Content-Type: application/json" \
  -d '{...}'

# Check order
curl http://localhost:8765/api/shop/orders/1000
```

---

## 📚 Documentation Files

| File | Purpose |
|------|---------|
| COMPLETE_SCHEMA_FLOW.md | Comprehensive schema + workflows |
| ERD_AND_FLOWS.md | Visual ERD + sequence diagrams |
| MIGRATION_GUIDE.md | MySQL → SQL Server guide |
| README.md | Setup instructions |
| *.sql | SQL creation scripts |

---

## 🚀 Getting Started (5 Steps)

1. **Run SQL Scripts** (in order)
   ```bash
   Execute: 01-product-catalog-db.sql
   Execute: 02-users-db.sql
   Execute: 03-orders-db.sql
   ```

2. **Update pom.xml** (add SQL Server dependency)
   ```xml
   <dependency>
       <groupId>com.microsoft.sqlserver</groupId>
       <artifactId>mssql-jdbc</artifactId>
       <version>12.2.0.jre11</version>
   </dependency>
   ```

3. **Update application.properties** (each service)
   - Use templates: application-sqlserver-*.properties
   - Update: url, username, password

4. **Build & Test** (ensure compile works)
   ```bash
   mvn clean package -DskipTests
   ```

5. **Start Services** (in order)
   - Eureka Server (8761)
   - API Gateway (8765)
   - User Service (8811)
   - Product Service (8810)
   - Order Service (8813)

---

**Last Updated**: May 6, 2026
**Version**: 1.0
**Status**: Production Ready ✅
