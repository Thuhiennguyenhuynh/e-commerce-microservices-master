# 🗄️ Complete Database Schema & System Flow

## 📊 Database Overview

```
┌─────────────────────────────────────────────────────────────────────────┐
│                       E-Commerce Microservices                          │
├──────────────────────┬──────────────────────┬──────────────────────────┤
│   Product Catalog    │    User Service      │    Order Service         │
│  (Port 8810)         │    (Port 8811)       │    (Port 8813)           │
├──────────────────────┼──────────────────────┼──────────────────────────┤
│ • categories         │ • user_roles         │ • products (ref)         │
│ • products           │ • users_details      │ • users (ref)            │
│ • product_images     │ • users              │ • inventory              │
│ • reviews            │                      │ • items                  │
│                      │                      │ • orders                 │
│                      │                      │ • cart                   │
└──────────────────────┴──────────────────────┴──────────────────────────┘
```

---

## 🏪 DATABASE 1: product_catalog

### 📋 Table: categories
**Purpose**: Danh mục sản phẩm

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PK, IDENTITY | Mã danh mục tự tăng |
| category_name | NVARCHAR(255) | NOT NULL, UNIQUE | Tên danh mục (duy nhất) |
| description | NVARCHAR(MAX) | NULL | Mô tả danh mục |

**Sample Data**:
```
id=1, category_name='Electronics', description='Electronic products'
id=2, category_name='Books', description='Books and publications'
id=3, category_name='Clothing', description='Apparel and fashion'
id=4, category_name='Food & Beverages', description='Food and beverage products'
```

---

### 📦 Table: products
**Purpose**: Thông tin sản phẩm

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PK, IDENTITY | Mã sản phẩm tự tăng |
| product_name | NVARCHAR(255) | NOT NULL | Tên sản phẩm |
| price | DECIMAL(19,2) | NOT NULL | Giá sản phẩm |
| discription | NVARCHAR(MAX) | NOT NULL | Mô tả chi tiết |
| image_url | NVARCHAR(MAX) | NOT NULL | URL ảnh chính |
| category_id | BIGINT | FK → categories(id) | Mã danh mục |
| availability | NVARCHAR(50) | DEFAULT 'AVAILABLE' | Trạng thái: AVAILABLE, OUT_OF_STOCK |
| quantity | INT | DEFAULT 0 | Số lượng tồn kho |
| created_at | DATETIME | DEFAULT GETDATE() | Ngày tạo |
| updated_at | DATETIME | DEFAULT GETDATE() | Ngày cập nhật |

**Indexes**:
- idx_products_category (category_id)
- idx_products_name (product_name)

**Relationships**:
- 1 category → many products
- 1 product → many product_images
- 1 product → many reviews

---

### 🖼️ Table: product_images
**Purpose**: Hình ảnh bổ sung của sản phẩm

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PK, IDENTITY | Mã ảnh tự tăng |
| image_url | NVARCHAR(MAX) | NOT NULL | URL hình ảnh |
| product_id | BIGINT | FK → products(id) | Mã sản phẩm (cascade delete) |
| created_at | DATETIME | DEFAULT GETDATE() | Ngày tạo |

**Indexes**:
- idx_product_images_product (product_id)

**Relationships**:
- Many images → 1 product

---

### ⭐ Table: reviews
**Purpose**: Đánh giá và bình luận của khách hàng

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PK, IDENTITY | Mã đánh giá tự tăng |
| product_id | BIGINT | FK → products(id) | Mã sản phẩm (cascade delete) |
| user_id | BIGINT | NULL | Mã người dùng (từ user-service) |
| rating | INT | NOT NULL, CHECK(1-5) | Điểm đánh giá 1-5 sao |
| comment | NVARCHAR(MAX) | NULL | Bình luận chi tiết |
| created_at | DATETIME | DEFAULT GETDATE() | Ngày tạo |

**Indexes**:
- idx_reviews_product (product_id)
- idx_reviews_user (user_id)

---

## 👤 DATABASE 2: users

### 👑 Table: user_roles
**Purpose**: Vai trò người dùng hệ thống

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PK, IDENTITY | Mã vai trò tự tăng |
| role_name | NVARCHAR(50) | NOT NULL, UNIQUE | Tên vai trò: ADMIN, USER, SELLER |
| description | NVARCHAR(255) | NULL | Mô tả vai trò |
| created_at | DATETIME | DEFAULT GETDATE() | Ngày tạo |

**Sample Data**:
```
id=1, role_name='ADMIN', description='Administrator'
id=2, role_name='USER', description='Regular User'
id=3, role_name='SELLER', description='Seller'
```

---

### 📝 Table: users_details
**Purpose**: Thông tin chi tiết người dùng (địa chỉ, liên hệ, ảnh đại diện)

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PK, IDENTITY | Mã chi tiết tự tăng |
| first_name | NVARCHAR(50) | NOT NULL | Tên đệm |
| last_name | NVARCHAR(50) | NOT NULL | Họ |
| email | NVARCHAR(100) | NOT NULL, UNIQUE | Email duy nhất |
| phone_number | NVARCHAR(15) | NULL | Số điện thoại |
| street | NVARCHAR(100) | NULL | Tên đường |
| street_number | NVARCHAR(10) | NULL | Số nhà |
| zip_code | NVARCHAR(10) | NULL | Mã bưu điện |
| locality | NVARCHAR(100) | NULL | Thành phố |
| country | NVARCHAR(100) | NULL | Quốc gia |
| image_url | NVARCHAR(MAX) | NULL | Đường dẫn ảnh đại diện người dùng |
| created_at | DATETIME | DEFAULT GETDATE() | Ngày tạo |
| updated_at | DATETIME | DEFAULT GETDATE() | Ngày cập nhật |

**Indexes**:
- idx_users_details_email (email)

**Relationships**:
- 1 user_details → 1 user

---

### 👥 Table: users
**Purpose**: Tài khoản đăng nhập người dùng

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PK, IDENTITY | Mã người dùng tự tăng |
| user_name | NVARCHAR(50) | NOT NULL, UNIQUE | Tên đăng nhập (duy nhất) |
| user_password | NVARCHAR(255) | NOT NULL | Mật khẩu (hashed) |
| active | INT | DEFAULT 1 | Trạng thái: 1=active, 0=inactive |
| user_details_id | BIGINT | FK → users_details(id) | Mã chi tiết người dùng (cascade delete) |
| role_id | BIGINT | FK → user_roles(id) | Mã vai trò |
| created_at | DATETIME | DEFAULT GETDATE() | Ngày tạo |
| updated_at | DATETIME | DEFAULT GETDATE() | Ngày cập nhật |

**Indexes**:
- idx_users_username (user_name)
- idx_users_active (active)
- idx_users_role (role_id)

**Relationships**:
- 1 user → 1 user_details
- 1 user_role → many users

---

## 🛒 DATABASE 3: orders

### 📦 Table: products (Reference)
**Purpose**: Reference đến sản phẩm (từ product-catalog-service)

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PK | Mã sản phẩm |
| product_name | NVARCHAR(255) | NOT NULL | Tên sản phẩm |
| price | DECIMAL(19,2) | NOT NULL | Giá sản phẩm |
| quantity | INT | DEFAULT 0 | Số lượng tồn kho |

---

### 👤 Table: users (Reference)
**Purpose**: Reference đến người dùng (từ user-service)

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PK | Mã người dùng |
| user_name | NVARCHAR(50) | NOT NULL | Tên đăng nhập |

---

### 📊 Table: inventory
**Purpose**: Quản lý tồn kho hàng hóa

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PK, IDENTITY | Mã tồn kho tự tăng |
| ingredient_name | NVARCHAR(255) | NOT NULL | Tên nguyên liệu/sản phẩm |
| unit | NVARCHAR(50) | NOT NULL | Đơn vị tính: kg, liter, box, etc |
| opening_stock | INT | NOT NULL, CHECK(>=0) | Tồn đầu ngày |
| stock_in | INT | NOT NULL, CHECK(>=0) | Số lượng nhập thêm |
| closing_stock | INT | NOT NULL, CHECK(>=0) | Tồn cuối ngày (= opening + in - out) |
| created_at | DATETIME | DEFAULT GETDATE() | Ngày tạo |
| updated_at | DATETIME | DEFAULT GETDATE() | Ngày cập nhật |

**Calculation**:
```
closing_stock = opening_stock + stock_in - (quantity sold from orders)
```

**Indexes**:
- idx_inventory_ingredient (ingredient_name)

---

### 🛍️ Table: items
**Purpose**: Sản phẩm trong đơn hàng

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PK, IDENTITY | Mã dòng sản phẩm tự tăng |
| quantity | INT | NOT NULL, CHECK(>0) | Số lượng mua |
| subtotal | DECIMAL(19,2) | NOT NULL, CHECK(>0) | Thành tiền = quantity × price |
| product_id | BIGINT | FK → products(id) | Mã sản phẩm |
| created_at | DATETIME | DEFAULT GETDATE() | Ngày tạo |

**Indexes**:
- idx_items_product (product_id)

**Relationships**:
- 1 product → many items
- Many items → many orders (through cart)

---

### 🎯 Table: orders
**Purpose**: Đơn hàng chính

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PK, IDENTITY | Mã đơn hàng tự tăng |
| ordered_date | DATE | DEFAULT GETDATE() | Ngày đặt hàng |
| status | NVARCHAR(50) | NOT NULL, DEFAULT 'PENDING' | Trạng thái đơn hàng |
| total | DECIMAL(19,2) | NOT NULL, CHECK(>0) | Tổng tiền |
| user_id | BIGINT | FK → users(id) | Mã người dùng |
| payment_method | NVARCHAR(50) | NULL | Phương thức thanh toán: CARD, TRANSFER, CASH |
| payment_status | NVARCHAR(50) | DEFAULT 'PENDING' | Trạng thái thanh toán: PENDING, PAID, FAILED |
| shipping_address | NVARCHAR(MAX) | NULL | Địa chỉ giao hàng |
| notes | NVARCHAR(MAX) | NULL | Ghi chú đặc biệt |
| created_at | DATETIME | DEFAULT GETDATE() | Ngày tạo |
| updated_at | DATETIME | DEFAULT GETDATE() | Ngày cập nhật |

**Status Values**:
- PENDING → AWAITING_PAYMENT → PAID → PROCESSING → SHIPPED → DELIVERED
- Any → CANCELLED (nếu user hủy)
- AWAITING_PAYMENT → FAILED (nếu thanh toán thất bại)

**Indexes**:
- idx_orders_user (user_id)
- idx_orders_status (status)
- idx_orders_date (ordered_date)

**Relationships**:
- 1 user → many orders
- Many items → 1 order (through cart)

---

### 🛒 Table: cart
**Purpose**: Join table giữa orders và items

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| order_id | BIGINT | PK, FK → orders(id) | Mã đơn hàng (cascade delete) |
| item_id | BIGINT | PK, FK → items(id) | Mã sản phẩm (cascade delete) |
| added_at | DATETIME | DEFAULT GETDATE() | Thời gian thêm vào giỏ |

**Indexes**:
- idx_cart_order (order_id)
- idx_cart_item (item_id)

**Relationships**:
- Many-to-Many: orders ↔ items

---

## 🔄 System Data Flow

### 1️⃣ User Registration Flow

```
┌─────────────────────────────────────────────────────────────────┐
│ Frontend (Vue) → POST /api/accounts/register                    │
├─────────────────────────────────────────────────────────────────┤
│ API Gateway (Port 8765)                                         │
│ Route: /accounts/** → http://user-service:8811                 │
├─────────────────────────────────────────────────────────────────┤
│ UserService (Port 8811)                                         │
│ 1. Call sp_RegisterUser stored procedure                        │
│ 2. Check if username exists → Error if yes                      │
│ 3. Check if email exists → Error if yes                         │
│ 4. Create users_details record                                  │
│ 5. Create users record with user_details_id + role_id           │
│ 6. Assign ROLE = USER by default (role_id = 2)                  │
├─────────────────────────────────────────────────────────────────┤
│ Database: users                                                  │
│ Tables: user_roles, users_details, users                        │
└─────────────────────────────────────────────────────────────────┘
```

### 2️⃣ Product Browsing Flow

```
┌─────────────────────────────────────────────────────────────────┐
│ Frontend (Vue) → GET /api/catalog/products?category=1           │
├─────────────────────────────────────────────────────────────────┤
│ API Gateway (Port 8765)                                         │
│ Route: /catalog/** → http://product-catalog-service:8810       │
├─────────────────────────────────────────────────────────────────┤
│ ProductCatalogService (Port 8810)                              │
│ 1. Call sp_GetProductsByCategory                                │
│ 2. Return products with category_id                             │
│ 3. Fetch product_images for each product                        │
│ 4. Fetch reviews and ratings                                    │
├─────────────────────────────────────────────────────────────────┤
│ Database: product_catalog                                       │
│ Tables: categories → products → product_images, reviews         │
└─────────────────────────────────────────────────────────────────┘
```

### 3️⃣ Shopping Cart & Order Creation Flow

```
┌────────────────────────────────────────────────────────────────────┐
│ Frontend (Vue) - Shopping Cart                                     │
│ [{id: 1, name: 'Laptop', qty: 1, price: 999}, ...]                │
├────────────────────────────────────────────────────────────────────┤
│ Frontend → POST /api/shop/orders                                   │
│ Payload: {user_id: 5, items: [...], shipping_address: "..."}      │
├────────────────────────────────────────────────────────────────────┤
│ API Gateway (Port 8765)                                            │
│ Route: /shop/** → http://order-service:8813                       │
├────────────────────────────────────────────────────────────────────┤
│ OrderService (Port 8813)                                           │
│ 1. Validate user exists (call user-service)                       │
│ 2. Validate products exist (call product-service)                 │
│ 3. FOR EACH item:                                                  │
│    a. Create Item record                                          │
│    b. Reserve inventory from inventory table                       │
│ 4. Create Order with status = PENDING                             │
│ 5. Link items to order via cart table                             │
│ 6. Return order_id and waiting for payment                        │
├────────────────────────────────────────────────────────────────────┤
│ Database: orders                                                   │
│ Tables: orders → cart → items → products (ref)                    │
│ Inventory: closing_stock -= quantity_ordered                      │
└────────────────────────────────────────────────────────────────────┘
```

### 4️⃣ Payment Processing Flow

```
┌────────────────────────────────────────────────────────────────────┐
│ Frontend → POST /api/shop/orders/{order_id}/payment                │
│ Payload: {payment_method: "CARD", ...payment_details}              │
├────────────────────────────────────────────────────────────────────┤
│ OrderService (Port 8813)                                           │
│ 1. Get order by order_id                                           │
│ 2. Check order.status == PENDING                                   │
│ 3. Call Payment Gateway (external)                                 │
│ 4. IF payment SUCCESS:                                             │
│    a. Update order.status = PAID                                   │
│    b. Update order.payment_status = PAID                           │
│    c. Update product.quantity (reduce from catalog-service)        │
│ 5. IF payment FAILED:                                              │
│    a. Update order.status = FAILED                                 │
│    b. Update order.payment_status = FAILED                         │
│    c. Release inventory (restore closing_stock)                    │
│    d. Return error message                                         │
├────────────────────────────────────────────────────────────────────┤
│ Database: orders                                                   │
│ Update: orders.status, orders.payment_status, inventory            │
└────────────────────────────────────────────────────────────────────┘
```

### 5️⃣ Product Upload & Image Management Flow

```
┌────────────────────────────────────────────────────────────────────┐
│ Frontend (Vue Admin) → POST /api/catalog/admin/upload              │
│ Payload: multipart/form-data {file: <image>}                       │
├────────────────────────────────────────────────────────────────────┤
│ API Gateway (Port 8765)                                            │
│ Route: /catalog/** → http://product-catalog-service:8810          │
├────────────────────────────────────────────────────────────────────┤
│ FileUploadController (Port 8810)                                   │
│ 1. Receive file upload                                             │
│ 2. Save to: uploads/images/{fileName}                              │
│ 3. Return: /images/{fileName} URL                                  │
├────────────────────────────────────────────────────────────────────┤
│ Frontend → POST /api/catalog/admin/products                        │
│ Payload: {                                                          │
│   productName: "Laptop",                                            │
│   price: 999,                                                       │
│   imageUrl: "/images/laptop_main.jpg",                              │
│   images: [                                                         │
│     {imageUrl: "/images/laptop_phu123_1.jpg"},                      │
│     {imageUrl: "/images/laptop_phu123_2.jpg"}                       │
│   ],                                                                │
│   categoryId: 1                                                     │
│ }                                                                   │
├────────────────────────────────────────────────────────────────────┤
│ ProductService (Port 8810)                                         │
│ 1. Create Product with imageUrl as main image                      │
│ 2. FOR EACH item in images array:                                  │
│    a. Create ProductImage record                                   │
│    b. Link to Product via product_id                               │
│ 3. Save all to database                                            │
├────────────────────────────────────────────────────────────────────┤
│ Database: product_catalog                                          │
│ Tables: products (1 main) → product_images (many extra)            │
└────────────────────────────────────────────────────────────────────┘
```

### 6️⃣ Order Status Flow

```
Order Lifecycle:
├─ PENDING
│  ├─ → AWAITING_PAYMENT (user initiates payment)
│  │    ├─ → PAID (payment success) → PROCESSING → SHIPPED → DELIVERED
│  │    └─ → FAILED (payment failed) → [Re-attempt or cancel]
│  └─ → CANCELLED (user cancels)
│
└─ Actions on status change:
   ├─ PENDING → AWAITING_PAYMENT: Lock order for modification
   ├─ AWAITING_PAYMENT → PAID: Update inventory, fulfill items
   ├─ AWAITING_PAYMENT → FAILED: Release inventory reservation
   └─ ANY → CANCELLED: Release inventory, refund payment
```

---

## 🔐 Key Relationships

### Product Catalog Database
```
categories
    ↓ (1:N)
products
    ├─ (1:N) product_images
    └─ (1:N) reviews
```

### Users Database
```
user_roles
    ↓ (1:N)
users
    ├─ (1:1) users_details
    └─ (N:1) user_roles
```

### Orders Database
```
users (ref)
    ↓ (1:N)
orders
    ├─ (N:N) items (via cart join table)
    └─ (N:1) products (ref)

inventory
    (manages stock for products)
```

---

## 📡 API Gateway Routing

```
API Gateway (Port 8765)
│
├─ /api/accounts/** 
│  └─ → user-service:8811 (user registration, login, profile)
│
├─ /api/catalog/**
│  └─ → product-catalog-service:8810 (products, categories, reviews)
│
├─ /api/shop/**
│  └─ → order-service:8813 (orders, cart, checkout)
│
└─ /api/review/**
   └─ → product-catalog-service:8810 (reviews - optional redirect)
```

---

## 💾 Stored Procedures Summary

### Product Catalog
| Procedure | Purpose | Input | Output |
|-----------|---------|-------|--------|
| sp_GetProductsByCategory | Lấy sản phẩm theo danh mục | @CategoryId | Products list |
| sp_GetProductWithImages | Lấy sản phẩm + ảnh | @ProductId | Product + images |

### Users
| Procedure | Purpose | Input | Output |
|-----------|---------|-------|--------|
| sp_GetUserByUsername | Tìm user theo username | @UserName | User data |
| sp_GetUserByEmail | Tìm user theo email | @Email | User data |
| sp_RegisterUser | Đăng ký user mới (tx) | @UserName, @Email, ... | Success/Error |

### Orders
| Procedure | Purpose | Input | Output |
|-----------|---------|-------|--------|
| sp_GetOrderWithItems | Lấy order + items | @OrderId | Order + items |
| sp_GetUserOrders | Lấy tất cả order của user | @UserId, @Status | Orders list |
| sp_CreateOrder | Tạo order mới (tx) | @UserId, @Total, ... | Order ID |
| sp_UpdateOrderStatus | Update trạng thái | @OrderId, @Status | Success/Error |
| sp_ReserveStock | Reserve inventory (tx) | @InventoryId, @Qty | Success/Error |
| sp_ReleaseStock | Release inventory (tx) | @InventoryId, @Qty | Success/Error |

---

## 🎯 Example Data Flow

### Scenario: User buys laptop

```
1. User: GET /api/catalog/categories
   → ProductService: SELECT * FROM categories
   ← Returns: [{id:1, name:'Electronics'}, ...]

2. User: GET /api/catalog/products?categoryId=1
   → ProductService: CALL sp_GetProductsByCategory(1)
   ← Returns: [{id:100, name:'Laptop', price:999, imageUrl:...}, ...]

3. User: GET /api/catalog/products/100
   → ProductService: CALL sp_GetProductWithImages(100)
   ← Returns: Product + [ProductImage1, ProductImage2, ...]

4. User: POST /api/shop/orders
   Body: {items: [{productId:100, qty:1}], shippingAddress:'123 Street'}
   → OrderService:
      a. Validate product 100 exists
      b. Create Item(quantity:1, subtotal:999, product_id:100)
      c. CREATE Order(total:999, user_id:5, status:'PENDING')
      d. INSERT INTO cart(order_id:NEW, item_id:NEW)
      e. UPDATE inventory SET closing_stock -= 1
   ← Returns: {orderId:1000, status:'PENDING', total:999}

5. User: POST /api/shop/orders/1000/payment
   Body: {paymentMethod:'CARD', cardNumber:'...'}
   → OrderService:
      a. GET Order(1000) where status='PENDING'
      b. Call PaymentGateway() → SUCCESS
      c. UPDATE orders SET status='PAID', payment_status='PAID'
      d. Call ProductCatalogService to reduce quantity
   ← Returns: {status:'PAID', message:'Payment successful'}

6. Admin: GET /api/shop/orders/1000/status
   → OrderService: SELECT * FROM orders WHERE id=1000
   ← Returns: {id:1000, status:'PAID', total:999, items:[...]}
```

---

## 🔄 Transaction Handling

### Critical Operations (with Transactions)

1. **User Registration** (sp_RegisterUser)
   ```sql
   BEGIN TRANSACTION
   - Check if username exists
   - Check if email exists  
   - INSERT into users_details
   - INSERT into users
   COMMIT/ROLLBACK
   ```

2. **Order Creation** (implicit in service)
   ```sql
   - Create Item records
   - Create Order record
   - Insert into cart table
   - Update inventory
   - Rollback if any fails
   ```

3. **Stock Reservation** (sp_ReserveStock)
   ```sql
   BEGIN TRANSACTION
   - Check stock availability
   - UPDATE inventory closing_stock -= qty
   COMMIT/ROLLBACK
   ```

4. **Payment Processing** (in service)
   ```sql
   IF payment SUCCESS:
       - Update order status to PAID
   ELSE:
       - Update order status to FAILED
       - Release reserved stock
   ```

---

## ⚠️ Data Validation & Constraints

| Table | Constraint | Rule |
|-------|-----------|------|
| products | price | > 0 |
| products | quantity | >= 0 |
| product_images | product_id | NOT NULL, FK cascade delete |
| reviews | rating | 1 ≤ rating ≤ 5 |
| users_details | email | UNIQUE |
| users | user_name | UNIQUE |
| users | active | 0 or 1 |
| items | quantity | > 0 |
| items | subtotal | > 0 |
| orders | total | > 0 |
| orders | status | IN ('PENDING', 'AWAITING_PAYMENT', 'PAID', ...) |
| inventory | stock | >= 0 (all columns) |

---

## 📈 Performance Optimization

### Indexes Created
```
Product Catalog:
- idx_products_category (category_id)
- idx_products_name (product_name)
- idx_product_images_product (product_id)
- idx_reviews_product (product_id)
- idx_reviews_user (user_id)

Users:
- idx_users_details_email (email)
- idx_users_username (user_name)
- idx_users_active (active)
- idx_users_role (role_id)

Orders:
- idx_inventory_ingredient (ingredient_name)
- idx_items_product (product_id)
- idx_orders_user (user_id)
- idx_orders_status (status)
- idx_orders_date (ordered_date)
- idx_cart_order (order_id)
- idx_cart_item (item_id)
```

All indexes configured for efficient querying and faster lookups.
