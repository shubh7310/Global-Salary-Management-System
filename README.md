# ACME HRMS — Global Salary Management System

[![Java 21](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot 3.3.4](https://img.shields.io/badge/Spring%20Boot-3.3.4-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/Database-PostgreSQL-blue.svg)](https://www.postgresql.org/)
[![Angular 18](https://img.shields.io/badge/Frontend-Angular%2018-red.svg)](https://angular.dev/)
[![Tests](https://img.shields.io/badge/Tests-18%20Passed-success.svg)](https://junit.org/junit5/)

**Author:** Shubhasheesh Shukla  
**Role:** Candidate — Software Craftsperson (Java / Angular II)  
**Company:** Incubyte Assessment Submission  

---

## 📌 Project Overview
ACME Corporation operates globally across **10 countries with 10,000 employees**. Previously, employee compensation was managed via fragmented spreadsheets. This system replaces that fragile process with an enterprise-grade, high-performance web platform that empowers the HR Manager to:
- Monitor organizational wage expenditure in real time with multi-currency normalization to USD.
- Search and filter across 10,000 employees with sub-50ms database-indexed queries.
- Inspect and adjust detailed salary components (Base, Allowances, Bonus, Deductions) with an immutable audit trail.
- Run "What-If" raise simulations to model financial budget impact before committing changes.
- Export filtered datasets to CSV.

---

## 🏗 Tech Stack
- **Backend:** Java 21, Spring Boot 3.3.4, Spring Data JPA, Hibernate, Bean Validation
- **Database:** PostgreSQL (with Docker Compose support + zero-setup embedded PostgreSQL compatibility profile for local evaluation)
- **Frontend:** Angular 18 (Standalone Components, Signals, Reactive HTTP), Tailwind CSS
- **Testing:** JUnit 5, Mockito, AssertJ, MockMvc (18 comprehensive unit and controller tests)

---

## 📚 Craftsmanship Documentation & Artifacts
The following comprehensive engineering artifacts are included in the repository:

| Document | Purpose |
| :--- | :--- |
| [**REQUIREMENTS.md**](file:///D:/hrms/REQUIREMENTS.md) | **One-Page Requirements Document**: Goal, Persona, Scope & Features, Deliberate Non-Goals & Rationale. |
| [**DEVELOPER.md**](file:///D:/hrms/DEVELOPER.md) | **Master Developer Guide**: Complete Code Flow, User Flow, Architecture Blueprint, Database Indexing, and Interview Walkthrough. |
| [**ARCHITECTURE.md**](file:///D:/hrms/ARCHITECTURE.md) | **System Architecture & ADRs**: Clean Architecture diagram, Domain Model, 10k Performance Strategy, REST API contracts. |
| [**TRADEOFFS.md**](file:///D:/hrms/TRADEOFFS.md) | **Engineering Trade-offs**: In-memory vs SQL aggregations, currency normalization, workflow engines vs audit logs. |
| [**AI_WORKFLOW.md**](file:///D:/hrms/AI_WORKFLOW.md) | **AI Collaboration Log**: Prompts used, critical engineering reviews, prompt refinements, and craftsmanship evolution. |

---

## 🚀 Quick Start Guide

### Option 1: Zero-Config 1-Command Execution (Recommended)
The Spring Boot backend comes bundled with the production-built Angular SPA:
```bash
cd backend
mvnw.cmd spring-boot:run
```
- Open **`http://localhost:8080`** in your browser.
- The web app automatically loads and seeds all 10,000 employees on first startup.

### Option 2: Running with Dockerized PostgreSQL
1. Start PostgreSQL:
   ```bash
   docker compose up -d
   ```
2. Start the backend with the `postgres` profile:
   ```bash
   cd backend
   mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=postgres
   ```
- Open **`http://localhost:8080`**.

### Option 3: Separate Angular Development Server
If you prefer running the Angular frontend with hot-reload:
```bash
# Terminal 1: Backend
cd backend
mvnw.cmd spring-boot:run

# Terminal 2: Angular Frontend
cd frontend
npm start
```
- Opens at **`http://localhost:4200`**.

---

## 🧪 Running Automated Tests
```bash
cd backend
mvnw.cmd test
```
All 18 unit tests, domain invariant checks, and MockMvc REST controller tests execute deterministically in ~7 seconds:
- `MoneyTest`: Validates Banker's Rounding (`HALF_EVEN`) and currency operations.
- `SalaryStructureTest`: Validates gross/net calculations, component non-negativity, and deduction constraints.
- `EmployeeTest`: Validates domain aggregate rules and salary revisions.
- `CurrencyConversionServiceTest`: Validates exchange rate conversions to USD.
- `EmployeeControllerTest`: Validates search, pagination, and detail endpoints.
- `AnalyticsControllerTest`: Validates executive KPI metrics and "What-If" simulations.

---

## 🛡 Performance & Scalability Highlights
- **Sub-50ms Query Times:** B-Tree composite indexes on `(country, department)` and `annual_gross_usd`.
- **Zero Loss Financial Precision:** Every currency calculation strictly uses Java `BigDecimal` with `RoundingMode.HALF_EVEN`.
- **Database-Level Aggregation:** KPI metrics, departmental sums, and country footprints are computed natively in SQL rather than streaming 10,000 objects into JVM heap memory.
- **Fast Seeding Engine:** Configured JDBC batch inserts populate 10,000 realistic employees across 10 countries and 8 departments in ~2 seconds.
- **Accessible & Responsive UX:** Clean enterprise styling without AI-purple clichés, fully responsive across mobile, tablet, and desktop.

