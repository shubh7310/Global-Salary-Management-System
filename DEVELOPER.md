# ACME HRMS — Developer Guide & System Flows

**System:** ACME Global Salary Management System (10,000 Employees)  
**Tech Stack:** Java 21 (Spring Boot 3) | PostgreSQL | Angular 18  

---

## 1. User Flow (HR Manager Workflow)

Elena Rostova (HR VP) uses this system to manage and audit global compensation. Here is her exact end-to-end flow:

### Flow Diagram

```
[ 1. HR Manager Logs In ]
          │
          ▼
[ 2. Executive Dashboard ]
   ├── Views Total Payroll ($1.28B), 10,000 Headcount, Mean ($128k), Median ($120k)
   ├── Inspects Department Budget Bars (Engineering, Sales, Product, etc.)
   └── Inspects Country Footprint Table (10 currencies converted to USD)
          │
          ▼
[ 3. Employee Directory ]
   ├── Searches by ID (EMP-00042), Name, or Title with sub-50ms response
   ├── Filters by Country, Department, or Salary Range
   └── Navigates 10,000 records using server-side pagination (Page 1 of 500)
          │
          ▼
[ 4. Inspect & Edit Employee Drawer ]
   ├── Clicks "Inspect & Edit" on any employee row
   ├── Drawer opens showing full salary breakdown:
   │     • Base Salary (70%)
   │     • Housing Allowance (12%)
   │     • Transport Allowance (4%)
   │     • Performance Bonus (14%)
   │     • Statutory Deductions (20%)
   │     • Monthly Net Take-Home
   ├── Updates component values in the revision form
   ├── Enters mandatory reason (e.g. "Annual Promotion")
   └── Clicks "Save" ──► Instant USD recalculation + Audit Log entry created
          │
          ▼
[ 5. What-If Salary Simulator ]
   ├── Selects Country (e.g. Germany) & Department (e.g. Engineering)
   ├── Enters proposed raise (e.g. +5%)
   └── Instantly sees:
         • Number of affected staff
         • Current Wage Bill vs Projected Wage Bill
         • Exact Budget Delta ($USD)
         • New Mean Salary
          │
          ▼
[ 6. Compliance Audit Trail ]
   └── Reviews timestamped log of all changes across the company with reasons & authors
```

### Step-by-Step Walkthrough

| Step | Page / Screen | What the HR Manager Does | What the System Does Behind the Scenes |
| :--- | :--- | :--- | :--- |
| **1. Executive Review** | `/dashboard` | Reviews company-wide payroll health, mean salary, and median benchmarks. | Runs SQL native aggregate functions (`SUM`, `AVG`, `MIN`, `MAX`) across 10,000 rows in <15ms. |
| **2. Regional Breakdown** | `/dashboard` | Checks local currencies (EUR, GBP, INR, JPY) alongside normalized USD figures. | Aggregates local payroll per country and converts using annual budget exchange rates. |
| **3. Search & Filter** | `/employees` | Searches for specific staff or filters by department/country. | Executes B-Tree indexed SQL query with pagination (`LIMIT 20 OFFSET 0`). |
| **4. Salary Revision** | `/employees` (Drawer) | Inspects employee compensation, adjusts components, and provides a business reason. | Validates non-negative amounts, checks deduction bounds, and saves an immutable audit record. |
| **5. Model Future Raises** | `/simulator` | Tests "What-If" scenarios (e.g. +6% raise for Sales). | Dynamically calculates budget delta and projected averages without modifying production records. |
| **6. Audit & Export** | `/audit-trail` & CSV | Audits historical revisions and downloads filtered CSV reports. | Streams CSV directly from database with proper escaping. |

---

## 2. Code Flow (Request Lifecycle)

When the browser requests data or modifies a salary, the call travels through 4 distinct layers:

```
+───────────────────────────────────────────────────────────────────+
| 1. PRESENTATION LAYER (Angular 18 Frontend)                       |
|    • Standalone Components: Dashboard, EmployeeList, Drawer, Sim  |
|    • Services: EmployeeService, AnalyticsService (HttpClient)     |
+─────────────────────────────────┬─────────────────────────────────+
                                  │ HTTP GET / POST / PUT (JSON)
                                  ▼
+───────────────────────────────────────────────────────────────────+
| 2. WEB API CONTROLLERS (Spring Boot 3)                            |
|    • EmployeeController       (/api/v1/employees)                 |
|    • AnalyticsController      (/api/v1/analytics)                 |
|    • DataExportController     (/api/v1/employees/export)          |
|    • Input Validation: @Valid, Bean Validation (@NotNull, @Min)   |
+─────────────────────────────────┬─────────────────────────────────+
                                  │ DTOs
                                  ▼
+───────────────────────────────────────────────────────────────────+
| 3. APPLICATION SERVICES & DOMAIN LOGIC                            |
|    • EmployeeService: Orchestrates search, updates, & audit logs  |
|    • CompensationAnalyticsService: KPI aggregates & simulations   |
|    • CurrencyConversionService: Normalizes 10 currencies to USD   |
|    • Pure Domain Core: Employee, SalaryStructure, Money (no ORM)  |
+─────────────────────────────────┬─────────────────────────────────+
                                  │ JPA Entities / Projections
                                  ▼
+───────────────────────────────────────────────────────────────────+
| 4. PERSISTENCE & DATABASE (PostgreSQL / H2)                       |
|    • EmployeeJpaRepository (Native SQL projections)               |
|    • CompensationAuditJpaRepository                               |
|    • B-Tree Composite Indexes on (country, department) & (usd)    |
|    • JDBC Batch Inserts (batch size: 500)                         |
+───────────────────────────────────────────────────────────────────+
```

### Detailed Layer Responsibilities

1. **Angular Frontend (`/frontend`):**
   - Pure standalone TypeScript components with reactive state.
   - Automatically detects dev mode (`port 4200`) vs unified backend mode (`port 8080`).
   - Clean enterprise design using Slate, Corporate Blue, and Emerald Green (no AI-purple styling).

2. **Spring Boot Controllers (`/presentation`):**
   - Validates incoming payloads (`@Valid`).
   - Delegates business operations to application services.
   - Handled by `GlobalExceptionHandler` returning structured RFC-7807 error responses.

3. **Application & Domain Services (`/application` + `/domain`):**
   - **`SalaryStructure`**: Pure Java domain value object. Encapsulates gross, net, and monthly take-home logic. Enforces business rules (deductions cannot exceed gross salary).
   - **`Money`**: Value object using `BigDecimal` and Banker's Rounding (`RoundingMode.HALF_EVEN`) to eliminate financial floating-point rounding errors.
   - **`CurrencyConversionService`**: Converts between 10 local currencies and corporate USD reporting currency.
   - **`DataSeedingService`**: Generates 10,000 realistic employees in batches of 500 records on first startup (~2 seconds).

4. **Persistence Layer (`/infrastructure`):**
   - **`EmployeeJpaRepository`**: Employs SQL aggregate projections (`COUNT`, `SUM`, `AVG`, `MIN`, `MAX`) directly in the database. Computes global metrics over 10,000 rows in <15ms.
   - **B-Tree Indexes**:
     - `idx_emp_country_dept` on `(country, department)`
     - `idx_emp_gross_usd` on `(annual_gross_usd)`
     - `idx_emp_code` on `(employee_code)`
     - `idx_emp_name` on `(last_name, first_name)`

---

## 3. Database Schema Overview

```
TABLE: employees
-----------------------------------------------------------------------------
id                  UUID (Primary Key)
employee_code       VARCHAR(32) UNIQUE, INDEXED
first_name          VARCHAR(64)
last_name           VARCHAR(64), INDEXED with first_name
email               VARCHAR(128)
department          VARCHAR(32), INDEXED
job_title           VARCHAR(100)
country             VARCHAR(32), INDEXED
hire_date           DATE
currency            VARCHAR(8) (USD, EUR, GBP, INR, JPY, CAD, AUD, SGD, CHF, BRL)
base_salary         NUMERIC(15, 2)
housing_allowance   NUMERIC(15, 2)
transport_allowance NUMERIC(15, 2)
performance_bonus   NUMERIC(15, 2)
statutory_deduction NUMERIC(15, 2)
annual_gross_local  NUMERIC(15, 2)
annual_net_local    NUMERIC(15, 2)
annual_gross_usd    NUMERIC(15, 2), INDEXED
annual_net_usd      NUMERIC(15, 2)

TABLE: compensation_audit_logs
-----------------------------------------------------------------------------
id                  UUID (Primary Key)
employee_id         UUID, INDEXED
employee_code       VARCHAR(32)
employee_name       VARCHAR(128)
currency            VARCHAR(8)
prev_gross_local    NUMERIC(15, 2)
new_gross_local     NUMERIC(15, 2)
prev_gross_usd      NUMERIC(15, 2)
new_gross_usd       NUMERIC(15, 2)
reason              VARCHAR(512)
modified_by         VARCHAR(64)
modified_at         TIMESTAMP, INDEXED
```

---

## 4. How to Run the Application

### Option A: One-Command Startup (Unified Port 8080)
The Spring Boot backend serves both the REST APIs and the pre-built Angular frontend:
```bash
cd backend
mvnw.cmd spring-boot:run
```
- Open browser at **`http://localhost:8080`**.
- 10,000 employees are seeded automatically on first startup.

### Option B: Real PostgreSQL with Docker
```bash
# 1. Start PostgreSQL
docker compose up -d

# 2. Start Backend with postgres profile
cd backend
mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=postgres
```

### Option C: Angular Development Mode (Port 4200)
```bash
cd frontend
npm start
```
- Opens at **`http://localhost:4200`** with live hot-reloading.

### Running Automated Tests
```bash
cd backend
mvnw.cmd test
```
All 18 unit, domain, and MockMvc controller tests pass in ~7 seconds.
