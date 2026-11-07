# ACME Global Salary Management System
## One-Page Requirements Document & Product Framing

**Author:** Shubhasheesh Shukla  
**Role:** Candidate — Software Craftsperson (Java / Angular II)  
**Target User Persona:** HR Manager (Elena Rostova — VP of People & Global HR Operations at ACME Corp)  
**Target Organization:** ACME Corp (10,000 Employees across 10+ countries)  

---

### 1. Problem Statement
ACME currently manages compensation data for **10,000 employees across multiple global jurisdictions** (USA, UK, Germany, India, Japan, Canada, Australia, Singapore, etc.) exclusively via scattered spreadsheets. 
This legacy approach creates severe operational friction:
1. **Zero Real-Time Visibility:** HR leadership cannot quickly answer fundamental organizational questions: *"What is our total global wage bill in USD?"*, *"How does Engineering compensation compare between the UK and Germany?"*, or *"Where do pay disparities exist?"*
2. **Formula Fragility & Version Drift:** Multi-currency conversions and complex salary structures (base pay, allowances, bonuses, statutory deductions) are vulnerable to accidental spreadsheet overrides and lack validation.
3. **Absence of Governance & Auditability:** Salary updates lack an immutable audit trail detailing who changed a figure, when, and the business rationale.
4. **Blind Adjustments:** HR cannot simulate the budget impact of annual cost-of-living adjustments before committing them.

---

### 2. Goal & Value Proposition
Build a centralized, high-performance, web-based **Employee Salary Management & Analytics System** that empowers the HR Manager to:
- Navigate, search, and audit 10,000 global employee salary records with sub-second responsiveness.
- Answer strategic questions regarding organization-wide compensation distribution in real time.
- Simulate and execute salary adjustments with full audit integrity and multi-currency normalization.

---

### 3. Key Questions the System Must Answer
| Question | Target Capability |
| :--- | :--- |
| **"What is our total annual payroll expenditure?"** | Real-time global payroll aggregation normalized to USD base reporting currency. |
| **"How does compensation distribute across departments?"** | Departmental summaries: Headcount, Total Spend, Average, Median, Min, and Max salary. |
| **"What are our country-level salary footprints?"** | Regional breakdown displaying local currency totals alongside normalized USD comparisons. |
| **"How is an individual employee's salary composed?"** | Component-level breakdown: Base Pay, Allowances, Performance Bonus, Deductions, Net Salary. |
| **"What is the budget impact of a proposed 5% raise for Engineering in Germany?"** | "What-If" Salary Adjustment Simulator calculating immediate payroll delta before commit. |

---

### 4. Scope & Core Features (In-Scope for MVP)
1. **Executive Compensation Analytics Dashboard:**
   - Real-time KPI Cards: Total Payroll (USD), Global Average Salary, Median Salary, Total Headcount (10,000).
   - Dynamic charts/visual breakdowns by Department, Country, and Salary Band.
   - Live currency conversion toggle (view in Base USD or native local currencies).
2. **High-Performance Employee Directory & Multi-Facet Filtering:**
   - Sub-100ms response over 10,000 records via indexed SQL queries and database pagination.
   - Multi-dimensional filters: Country, Department, Job Title, Currency, Salary Range.
   - Instant search by Employee ID, Full Name, or Role.
3. **Comprehensive Employee Salary Profile & Drawer:**
   - Transparent display of salary structure components: Base Salary, Housing/Transport Allowance, Performance Bonus, Statutory Deductions, and Net Annual/Monthly Take-Home.
   - Historical salary timeline and audit notes.
4. **Salary Adjustment & "What-If" Simulator:**
   - Individual salary revision workflow with mandatory reason documentation.
   - Bulk department/region adjustment simulation displaying immediate budget impact.
5. **Data Seeding & Interoperability:**
   - Automated, deterministic seeding engine populating **10,000 realistic employee records** across 10 countries, 8 departments, and 4 salary bands.
   - CSV export capability for downstream reporting.

---

### 5. Deliberately Leaving Out (Non-Goals) & Architectural Reasoning

| Deliberate Non-Goal | Rationale & Architectural Justification |
| :--- | :--- |
| **Direct Banking / Payment Rails (ACH, SEPA, SWIFT)** | This system is a **compensation governance and analytical decision platform**, not a disbursement engine. Integrating banking APIs introduces PCI/banking compliance overhead, latency, and third-party failure points that distract from core HR analytics. |
| **Complex BPMN Approval Engine (e.g., Camunda/Activiti)** | For the MVP, a heavyweight workflow engine introduces table-locking bottlenecks and steep operational complexity. Instead, an immutable **Audit Trail with Maker-Notes** provides 90% of the governance value at 10% of the architectural weight. |
| **Time & Attendance / Punch-Clock Tracking** | Attendance tracking belongs in a dedicated operational HRIS. Mixing punch-clock logic into salary governance violates Domain-Driven Design (Bounded Contexts) and pollutes the domain model. |
| **Enterprise SSO / Identity Provider Integration (Okta/SAML)** | Kept decoupled to preserve developer ergonomics and zero-friction evaluation. Role-context headers (`X-HR-User`) simulate authenticated session context without requiring cloud infrastructure. |

---

### 6. Success Metrics & Craftsmanship Criteria
- **Performance:** Complex aggregations across all 10,000 employees execute in under 150ms.
- **Data Integrity:** Zero precision loss in financial computations using `BigDecimal` and strict currency types.
- **Test Quality:** Deterministic unit and integration test suite covering domain rules, aggregations, and edge cases.
- **Maintainability:** Clean Architecture with clear separation between Domain, Application, and Web layers.
