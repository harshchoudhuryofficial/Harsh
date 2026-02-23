# Advanced JDBC Student Management

A production-style JDBC mini-project demonstrating:

- Clean DAO + service layering
- Batch inserts for high-throughput student onboarding
- Transactional enrollment flow with rollback safety
- Prepared statements and aggregate queries
- Pagination and filtered search
- Relational schema with foreign keys

## Tech Stack

- Java 17
- JDBC
- H2 in-memory database
- Maven

## Project Structure

```text
src/main/java/com/harsh/studentmgmt
├── Main.java
├── config/DatabaseConfig.java
├── dao/
│   ├── CourseDao.java
│   └── StudentDao.java
├── model/
│   ├── Course.java
│   └── Student.java
├── service/EnrollmentService.java
└── util/SchemaInitializer.java
```

## Run

```bash
mvn clean compile exec:java
```

## Core Advanced JDBC Features Used

1. **Batch processing**
   - `StudentDao.batchCreate(...)` uses `addBatch/executeBatch` with manual transaction boundaries.

2. **Transactions**
   - `EnrollmentService.enrollStudent(...)` uses `setAutoCommit(false)`, `commit`, and rollback on any exception.

3. **Prepared statements and SQL safety**
   - Every query uses `PreparedStatement` to avoid SQL injection and support parameterized execution.

4. **Pagination + sorting**
   - `StudentDao.findByDepartment(...)` demonstrates `LIMIT/OFFSET` and deterministic ordering.

5. **Schema bootstrap**
   - `SchemaInitializer` executes `schema.sql` at startup for reproducible setup.

## How to adapt for MySQL/PostgreSQL

- Replace JDBC URL/credentials in `DatabaseConfig`.
- Add corresponding driver dependency in `pom.xml`.
- Keep DAO/service code unchanged.

