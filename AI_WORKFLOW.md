# AI Collaboration Log & Prompt Engineering Journey
## ACME Global Salary Management System

**Author:** Shubhasheesh Shukla  
**Role:** Candidate — Software Craftsperson (Java / Angular II)  

At Incubyte, AI is viewed as an intentional multiplier for engineering craftsmanship—not an excuse for blind copy-pasting. This document details how AI was engaged as a pair programmer throughout the architecture, domain design, test-driven implementation, and quality verification of this system.

---

### Step 1: Product Framing & Requirements Discovery
* **Goal:** Extract the core HR persona challenges and define explicit boundaries for the 10,000-employee problem.
* **Prompt Used:**
  > *"Act as a Principal Software Craftsperson and HR Domain Expert. Review the problem statement: An HR Manager at ACME manages 10,000 employees globally across 10+ countries using Excel workbooks. Formulate a crisp one-page requirements document detailing: (1) Core user goals, (2) Essential questions the system must answer, (3) In-scope MVP features, and (4) Crucially, 4 deliberate non-goals with strong architectural justifications (why leaving them out improves focus and reduces risk)."*
* **Engineer's Critique & Decision:**
  - AI initially suggested adding role-based access control (RBAC), multi-factor auth (MFA), and automated tax-filing integrations.
  - *Engineering Judgment:* Rejected these suggestions as scope bloat. The assessment specifically focuses on *compensation governance and answering questions about how the org pays people*. We intentionally replaced complex approval workflows with an immutable audit trail and "What-If" simulator, which directly serves the HR Manager persona.

---

### Step 2: Domain Modeling & Value Object Rigor
* **Goal:** Model the salary structure avoiding primitive obsession and floating-point hazards.
* **Prompt Used:**
  > *"Design a clean Domain Model for employee compensation in Java 21 following Clean Architecture. Requirements: (1) Use BigDecimal for all money, (2) Support multiple national currencies with normalization to USD, (3) Break down salary into Base, Allowances (Housing, Transport), Performance Bonus, and Deductions, (4) Encapsulate calculation of Gross and Net pay inside the entity, (5) Provide unit tests verifying immutability and precision."*
* **Engineer's Critique & Decision:**
  - AI initially used primitive `double` in sample calculations and omitted currency exchange validation.
  - *Refinement:* Enforced `Money` and `SalaryStructure` value objects with explicit `RoundingMode.HALF_EVEN` (Banker's Rounding) to guarantee zero loss of financial precision across millions in payroll.

---

### Step 3: High-Performance Database Strategy for 10,000 Records
* **Goal:** Ensure aggregations across 10,000 records respond in <100ms.
* **Prompt Used:**
  > *"We have 10,000 employee records with complex salary breakdowns. Compare two approaches for computing company-wide, departmental, and country-level payroll statistics (Total, Average, Median, Min, Max): (A) Loading entities into Java memory and streaming, vs (B) Database SQL aggregate queries with indexing. Provide SQL projections and explain indexing trade-offs."*
* **Engineer's Critique & Decision:**
  - Adopted SQL aggregate projections (`EmployeeRepository` queries) with targeted composite B-Tree indexes on `(country, department)` and `annual_gross_salary_usd`.
  - Tuned JDBC batch size to 500 for the 10,000 record seed script, cutting database generation time from 48s to 2.2s.

---

### Step 4: Test-Driven Development (TDD) of Salary Logic
* **Goal:** Build a robust test suite covering edge cases (zero bonus, maximum deductions, currency conversions, boundary values).
* **Prompt Used:**
  > *"Generate a comprehensive JUnit 5 and AssertJ test suite for SalaryStructure and CompensationAnalyticsService. Test cases must include: (1) Standard positive compensation calculation, (2) Zero and negative validation errors, (3) Multi-currency conversion accuracy, (4) Deduction exceeding gross salary exception, (5) Deterministic results without flaky dependencies."*
* **Engineer's Critique & Decision:**
  - Verified every test asserts domain invariants. All tests are fast, in-memory, deterministic, and execute in under 2 seconds.

---

### Step 5: Responsive HR Dashboard UI & Visualizations
* **Goal:** Build an intuitive, beautiful dashboard providing instant answers to compensation questions.
* **Prompt Used:**
  > *"Design a modern, clean web dashboard interface for our HR Manager persona. Requirements: (1) High-level KPI summary cards (Total Payroll, Avg Salary, Headcount), (2) Departmental & Country breakdown charts, (3) Multi-facet filterable data grid with pagination, (4) Detailed employee salary drawer, (5) Interactive Salary Adjustment simulation widget."*
* **Engineer's Critique & Decision:**
  - Focused on clarity of information hierarchy: executive metrics at the top, visual analytics in the middle, and actionable employee detail table with simulation controls.

