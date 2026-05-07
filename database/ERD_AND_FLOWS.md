# 📊 Entity Relationship Diagram (ERD)

## Database: product_catalog

```
┌─────────────────────┐
│   categories        │
├─────────────────────┤
│ id (PK)             │
│ category_name (U)   │
│ description         │
└──────────┬──────────┘
           │ 1:N
           │
           ↓
┌─────────────────────────────────┐
│        products                 │
├─────────────────────────────────┤
│ id (PK)                         │
│ product_name                    │
│ price                           │
│ discription                     │
│ image_url                       │
│ category_id (FK)    ────┐       │
│ availability                    │
│ quantity                        │
│ created_at                      │
│ updated_at                      │
└──────────┬──────────┬───────────┘
           │ 1:N      │ 1:N
           │          │
      ┌────┴────┐  ┌──┴──────────────┐
      ↓         ↓  ↓                 ↓
┌──────────────────────┐    ┌────────────────┐
│  product_images      │    │    reviews     │
├──────────────────────┤    ├────────────────┤
│ id (PK)              │    │ id (PK)        │
│ image_url            │    │ product_id (FK)│
│ product_id (FK)      │    │ user_id        │
│ created_at           │    │ rating (1-5)   │
└──────────────────────┘    │ comment        │
                            │ created_at     │
                            └────────────────┘
```

## Database: users

```
┌─────────────────────┐
│   user_roles        │
├─────────────────────┤
│ id (PK)             │
│ role_name (U)       │
│ description         │
│ created_at          │
└────────┬────────────┘
         │ 1:N
         │
         ↓
┌─────────────────────────────┐
│        users                │
├─────────────────────────────┤
│ id (PK)                     │
│ user_name (U)               │
│ user_password               │
│ active                      │
│ user_details_id (FK)  ──┐   │
│ role_id (FK)     ──┐   │   │
│ created_at           │   │   │
│ updated_at           │   │   │
└─────────────────────┼───┼───┘
                      │ 1:1
        1:N           │
         │           ↓
         └──→ ┌──────────────────┐
              │  users_details   │
              ├──────────────────┤
              │ id (PK)          │
              │ first_name       │
              │ last_name        │
              │ email (U)        │
              │ phone_number     │
              │ street           │
              │ street_number    │
              │ zip_code         │
              │ locality         │
              │ country          │
              │ created_at       │
              │ updated_at       │
              └──────────────────┘
```

## Database: orders

```
┌──────────────────────────┐
│   products (ref)         │
├──────────────────────────┤
│ id (PK)                  │
│ product_name             │
│ price                    │
│ quantity                 │
└──────────┬───────────────┘
           │ 1:N
           │
      ┌────┴──────────────┐
      ↓                   ↓
┌──────────────┐    ┌────────────────────┐
│    items     │    │   inventory        │
├──────────────┤    ├────────────────────┤
│ id (PK)      │    │ id (PK)            │
│ quantity     │    │ ingredient_name    │
│ subtotal     │    │ unit               │
│ product_id   │    │ opening_stock      │
│ created_at   │    │ stock_in           │
└──────┬───────┘    │ closing_stock      │
       │ N:N        │ created_at         │
       │            │ updated_at         │
       ↓            └────────────────────┘
┌────────────────┐
│     cart       │
├────────────────┤
│ order_id (FK)  │
│ item_id (FK)   │
│ added_at       │
└────────┬───────┘
         │ N:1
         │
         ↓
┌────────────────────────────────┐
│         orders                 │
├────────────────────────────────┤
│ id (PK)                        │
│ ordered_date                   │
│ status                         │
│ total                          │
│ user_id (FK)           ──┐     │
│ payment_method            │     │
│ payment_status            │     │
│ shipping_address          │     │
│ notes                     │     │
│ created_at                │     │
│ updated_at                │     │
└───────────────────────────┼─────┘
                            │ N:1
                            ↓
                ┌──────────────────────┐
                │   users (ref)        │
                ├──────────────────────┤
                │ id (PK)              │
                │ user_name            │
                └──────────────────────┘
```

## Complete Microservices Architecture

```
┌──────────────────────────────────────────────────────────────────────────┐
│                         Frontend (Vue.js)                                │
│                 http://localhost:3000                                    │
└─────────────────────────────┬──────────────────────────────────────────┘
                              │ HTTP/REST API
                              ↓
┌──────────────────────────────────────────────────────────────────────────┐
│                    API Gateway (Zuul)                                    │
│                    http://localhost:8765                                 │
├──────────────────────────────────────────────────────────────────────────┤
│  Routes:                                                                 │
│  /api/accounts/** ──→ user-service:8811                                 │
│  /api/catalog/**  ──→ product-catalog-service:8810                      │
│  /api/shop/**     ──→ order-service:8813                                │
│  /api/review/**   ──→ product-catalog-service:8810                      │
└──────────┬──────────────────┬─────────────────────────┬──────────────────┘
           │                  │                         │
    ┌──────↓─────┐    ┌──────↓──────────┐    ┌─────────↓──────────┐
    │ User        │    │ Product Catalog │    │ Order Service     │
    │ Service     │    │ Service         │    │                   │
    │ Port: 8811  │    │ Port: 8810      │    │ Port: 8813        │
    └──────┬─────┘    └──────┬──────────┘    └─────────┬──────────┘
           │                 │                         │
    ┌──────↓────────────────────────────────────────────↓────────┐
    │                  SQL Server (localhost:1433)                │
    ├─────────────────────────────────────────────────────────────┤
    │                                                              │
    │  ┌────────────────┐  ┌────────────────┐  ┌──────────────┐  │
    │  │ users (DB)     │  │ product_       │  │ orders (DB)  │  │
    │  │                │  │ catalog (DB)   │  │              │  │
    │  ├────────────────┤  ├────────────────┤  ├──────────────┤  │
    │  │ user_roles     │  │ categories     │  │ products(ref)│  │
    │  │ users_details  │  │ products       │  │ users(ref)   │  │
    │  │ users          │  │ product_images │  │ inventory    │  │
    │  │                │  │ reviews        │  │ items        │  │
    │  │                │  │                │  │ orders       │  │
    │  │                │  │                │  │ cart         │  │
    │  └────────────────┘  └────────────────┘  └──────────────┘  │
    │                                                              │
    └──────────────────────────────────────────────────────────────┘

    ┌──────────────────────────────────────┐
    │ Service Discovery (Eureka)           │
    │ http://localhost:8761                │
    ├──────────────────────────────────────┤
    │ - user-service (8811)                │
    │ - product-catalog-service (8810)     │
    │ - order-service (8813)               │
    │ - api-gateway (8765)                 │
    │ - eureka-server (8761)               │
    └──────────────────────────────────────┘

    ┌──────────────────────────────────────┐
    │ Session Store (Redis)                │
    │ http://localhost:6379                │
    └──────────────────────────────────────┘

    ┌──────────────────────────────────────┐
    │ File Storage                         │
    │ uploads/images/                      │
    │ (served by product-catalog-service)  │
    └──────────────────────────────────────┘
```

## Data Flow Sequences

### 1. User Registration Sequence

```
Frontend          API Gateway       UserService        Database
   │                  │                  │                 │
   │─ POST /register ─→│                  │                 │
   │                  │─ POST /register ─→│                 │
   │                  │                  │─ sp_RegisterUser→│
   │                  │                  │ ✓ Create users_details
   │                  │                  │ ✓ Create users   │
   │                  │                  │←─ Success       │
   │                  │←─ 200 OK ────────│                 │
   │←─ 200 OK ────────│                  │                 │
   │                  │                  │                 │
```

### 2. Product Browsing Sequence

```
Frontend          API Gateway    ProductService      Database
   │                  │                  │                 │
   │─ GET /products ─→│                  │                 │
   │                  │─ GET /products ─→│                 │
   │                  │                  │─ Query categories
   │                  │                  │←─ categories list
   │                  │                  │─ Query products  │
   │                  │                  │←─ products list  │
   │                  │                  │─ Query images   │
   │                  │                  │←─ images list    │
   │                  │←─ JSON array ────│                 │
   │←─ Render list ───│                  │                 │
   │                  │                  │                 │
```

### 3. Order Checkout Sequence

```
Frontend      API Gateway    OrderService    UserService    Database
   │              │                │              │             │
   │─POST /orders→│                │              │             │
   │              │─POST /orders ──→│              │             │
   │              │                 │──Validate ──→│             │
   │              │                 │ user exists  │             │
   │              │                 │←─ User OK ───│             │
   │              │                 │              │─ Create Item
   │              │                 │              │             │
   │              │                 │─────────────────→ Reserve  │
   │              │                 │ Inventory   │    stock    │
   │              │                 │─ Create Order ──→ ✓ Insert │
   │              │                 │─ Insert Cart  ──→ ✓ Link   │
   │              │                 │              │             │
   │              │←─ 201 Order ────│              │             │
   │←─ Order ID ──│                 │              │             │
   │              │                 │              │             │

   Later: Payment Processing
   
   │─POST /payment→│                │              │             │
   │              │─POST /payment ──→│              │             │
   │              │                 │─ Call Payment Gateway
   │              │                 │ (External)   │             │
   │              │                 │← ✓ Success   │             │
   │              │                 │─ Update Order Status ──→ ✓  │
   │              │                 │─ Update Payment Status ──→ ✓ │
   │              │                 │─ Update Inventory ────→ ✓    │
   │              │←─ 200 Paid ─────│              │             │
   │←─ Success ───│                 │              │             │
```

### 4. Product Upload Sequence

```
Admin(Vue)    API Gateway    ProductService       FileSystem    Database
   │              │                │                    │           │
   │─Upload file ─→│                │                    │           │
   │              │─Upload file ───→│                    │           │
   │              │                 │─ Save file ──────→│           │
   │              │                 │←─ /images/name.jpg│           │
   │              │                 │                    │           │
   │─POST product ─→│                │                    │           │
   │   (with URL)  │─POST product ──→│                    │           │
   │              │                 │─ Create Product ─────────→ ✓   │
   │              │                 │─ FOR images in array:     │   │
   │              │                 │  Create ProductImage ────→ ✓   │
   │              │                 │                    │           │
   │              │←─ 201 Created ──│                    │           │
   │←─ Success ───│                 │                    │           │
```

## Table Relationship Summary

| From | To | Relationship | Cascade | Purpose |
|------|-----|-------------|---------|---------|
| categories | products | 1:N | - | Group products by category |
| products | product_images | 1:N | YES (DELETE) | Store multiple images per product |
| products | reviews | 1:N | YES (DELETE) | Store product reviews |
| user_roles | users | 1:N | - | Assign roles to users |
| users_details | users | 1:1 | YES (DELETE) | User personal information |
| users | orders | 1:N | - | Track user orders |
| products (ref) | items | 1:N | - | Items reference products |
| items | orders | N:N (via cart) | YES (DELETE) | Cart contains items |
| users (ref) | orders | N:1 | - | Associate order to user |

## Query Examples

### Get all products in Electronics category
```sql
EXEC sp_GetProductsByCategory @CategoryId = 1
```

### Get product with all its images
```sql
EXEC sp_GetProductWithImages @ProductId = 100
```

### Get user by username with all details
```sql
EXEC sp_GetUserByUsername @UserName = 'john_doe'
```

### Create new order with transaction
```sql
EXEC sp_CreateOrder 
    @UserId = 5,
    @OrderedDate = CAST(GETDATE() AS DATE),
    @Total = 1500.00,
    @Status = 'PENDING'
```

### Reserve stock for order
```sql
EXEC sp_ReserveStock 
    @InventoryId = 10,
    @Quantity = 5
```

### Get user orders by status
```sql
EXEC sp_GetUserOrders 
    @UserId = 5,
    @Status = 'PENDING'
```
