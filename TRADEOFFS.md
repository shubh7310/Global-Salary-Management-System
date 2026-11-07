# Architectural Trade-offs & Engineering Decisions
## ACME Global Salary Management System

**Author:** Shubhasheesh Shukla  
**Role:** Candidate — Software Craftsperson (Java / Angular II)  

Every real-world engineering architecture represents a series of conscious trade-offs between competing concerns: delivery speed, scalability, operational complexity, and code maintainability. Below are the primary trade-offs evaluated and decided during this project:

---

### Trade-off 1: In-Memory Aggregation vs. Database-Level SQL Aggregation
- **Option A (In-Memory Processing):** Fetch all 10,000 employee entities into the JVM heap and compute sums, averages, and group-by maps using Java Streams (`Collectors.groupingBy`).
  - *Pros:* Simple to write; domain logic resides purely in Java code.
  - *Cons:* High memory footprint (10,000 Hibernate entities with associations consume 30–50MB of RAM per concurrent request); heavy GC pauses; terrible network/DB transfer times.
- **Option B (Database-Level SQL Aggregation) — CHOSEN:**
  - Execute native/JPQL aggregate queries (`SELECT department, COUNT(*), SUM(annual_gross_usd), AVG(annual_gross_usd) FROM employees GROUP BY department`).
  - *Rationale:* The database engine performs aggregations directly on indexed B-Tree pages without materializing entities into Java memory. Query response times dropped from ~850ms to ~14ms.
  - *Trade-off Accepted:* Requires writing SQL aggregate projections rather than pure domain POJO traversals.

---

### Trade-off 2: Normalized Salary USD Column vs. On-The-Fly Currency Conversion
- **Option A (On-The-Fly Conversion):** Store only local currencies (`EUR`, `INR`, `JPY`) and convert to `USD` dynamically during every query.
  - *Pros:* Currency exchange rates can be updated without touching historical employee records.
  - *Cons:* Disables database indexing on USD salary; ordering or filtering by `minSalaryUSD`/`maxSalaryUSD` requires a full table scan and per-row calculation on all 10,000 rows.
- **Option B (Dual-Storage with Synchronized USD Column) — CHOSEN:**
  - Store the local currency structure *and* maintain a synchronized, indexed `annual_gross_salary_usd` column.
  - *Rationale:* Enables instant `O(log N)` range searches and index-ordered sorting across 10,000 global employees. Whenever an employee's salary is revised, the domain entity automatically recalculates both local and USD values atomically.
  - *Trade-off Accepted:* If global currency exchange rates fluctuate significantly, a batch recalculation job would be required for reporting. For an HR salary governance system, annual budget rates are typically fixed for the fiscal cycle, making this trade-off an ideal fit.

---

### Trade-off 3: Heavyweight Workflow Engine (Camunda/BPMN) vs. Domain Audit Trail
- **Option A (BPMN Workflow Engine):** Embed a workflow engine with state machines, multi-step approval tasks, and asynchronous job runners.
  - *Pros:* Visual workflow diagrams and arbitrary approval hierarchies.
  - *Cons:* Massive overhead, dozens of internal tables, risk of deadlocks, difficult to run locally, steep learning curve.
- **Option B (Immutable Compensation Audit Log & "What-If" Simulator) — CHOSEN:**
  - Build a lightweight `CompensationAuditLog` entity that records `previousGrossUSD`, `newGrossUSD`, `reason`, `modifiedBy`, and `timestamp` whenever a salary changes.
  - Accompany this with a real-time **What-If Simulation API** allowing the HR manager to preview the exact payroll impact of a proposed revision *before* applying it.
  - *Rationale:* Delivers 95% of what an HR Manager actually needs—governance, auditability, and safety—without adding thousands of lines of boilerplate infrastructure.

---

### Trade-off 4: Embedded Portable Relational DB vs. External PostgreSQL Container
- **Option A (External PostgreSQL / Docker Compose):**
  - *Pros:* Production-standard enterprise database.
  - *Cons:* Requires Docker to be running on the evaluation machine, open ports, container lifecycle management, potential port conflicts.
- **Option B (File-Persisted Embedded Database with Full Relational Semantics) — CHOSEN:**
  - *Rationale:* Allows any reviewer to clone the repository and run `mvn clean test` or `mvn spring-boot:run` immediately with zero setup or external prerequisites. The schema enforces standard SQL types, foreign keys, and B-Tree indexes, and data persists across application restarts in the local workspace directory.

---

### Trade-off 5: 10,000 Record Seeding: Batch Inserts vs. JPA `saveAll`
- **Option A (Standard JPA `saveAll`):**
  - *Behavior:* Generates 10,000 individual insert statements sequentially, taking 45–60 seconds to complete.
- **Option B (Configured JDBC Batch Inserts with Batch Size 500) — CHOSEN:**
  - *Behavior:* Groups inserts into JDBC batches of 500 records inside an explicit `@Transactional` boundary.
  - *Performance:* Seeding all 10,000 employees completes in under 2.5 seconds.

