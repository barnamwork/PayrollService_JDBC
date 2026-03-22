# Employee Payroll Service — Java JDBC

A Java application that manages employee payroll data using JDBC and a MySQL backend.
The project covers full CRUD operations, ER modeling, database normalization,
and SQL query execution via the Java JDBC API.

---

## Table of Contents

- [Overview](#overview)
- [Tech Stack](#tech-stack)
- [Database Setup](#database-setup)
- [Schema](#schema)
- [Use Cases](#use-cases)
- [JDBC Architecture](#jdbc-architecture)
- [Getting Started](#getting-started)
- [Project Structure](#project-structure)
- [Git Flow](#git-flow)
- [Sample Queries](#sample-queries)

---

## Overview

This project demonstrates how to design and interact with a relational MySQL database
from a Java application. It walks through creating the `payroll_service` database,
building a normalized schema based on an ER diagram, and performing all CRUD operations
using JDBC `Statement` and `PreparedStatement`.

---

## Tech Stack

| Tool | Version |
|------|---------|
| Java | JDK 25 |
| MySQL | 8.0+ |
| MySQL Connector/J | 8.0.33 |
| Maven | Latest |
| IntelliJ IDEA | Latest |

---

## Database Setup

```sql
CREATE DATABASE payroll_service;
USE payroll_service;

CREATE TABLE employee_payroll (
    id           INT UNSIGNED NOT NULL AUTO_INCREMENT,
    name         VARCHAR(150) NOT NULL,
    phone_number VARCHAR(250),
    address      VARCHAR(250) DEFAULT 'TBD',
    department   VARCHAR(150) NOT NULL,
    gender       CHAR(1),
    basic_pay    DOUBLE NOT NULL,
    deductions   DOUBLE NOT NULL,
    taxable_pay  DOUBLE NOT NULL,
    tax          DOUBLE NOT NULL,
    net_pay      DOUBLE NOT NULL,
    start        DATE NOT NULL,
    PRIMARY KEY  (id)
);
```

---

## Schema

The final schema is derived from an ER diagram with the following entities and relationships:

| Entity   | Relationship | Entity     |
|----------|-------------|------------|
| Company  | One-to-Many  | Employee   |
| Employee | Many-to-Many | Department |
| Employee | One-to-One   | Payroll    |

For the Many-to-Many relationship between Employee and Department,
a junction table `employee_department` is used with `employee_id` and `department_id`.

### Normalized Tables

```
employee            → employee_id, name, gender, phone_number, address, start
payroll             → payroll_id, employee_id, basic_pay, deductions, taxable_pay, tax, net_pay
department          → department_id, department_name
employee_department → employee_id, department_id
```

---

## Use Cases

### Section 1 — MySQL DB

| UC  | Branch | Description |
|-----|--------|-------------|
| UC1 | `feature/UC1-createDatabase` | Create the `payroll_service` database |
| UC2 | `feature/UC2-createEmployeePayrollTable` | Create `employee_payroll` table |
| UC3 | `feature/UC3-insertEmployeePayrollData` | Insert employee records using `INSERT INTO` |
| UC4 | `feature/UC4-retrieveAllEmployeePayroll` | Retrieve all records using `SELECT *` |
| UC5 | `feature/UC5-retrieveSalaryByNameAndDateRange` | Filter by name and date range |
| UC6 | `feature/UC6-addGenderAndUpdate` | Add `gender` column via `ALTER TABLE` |
| UC7 | `feature/UC7-genderBasedAggregation` | SUM, AVG, MIN, MAX, COUNT grouped by gender |

### Section 2 — ER Diagram

| UC   | Branch | Description |
|------|--------|-------------|
| UC8  | `feature/UC8-extendEmployeePayrollData` | Add `phone_number`, `address`, `department` |
| UC9  | `feature/UC9-extendPayrollDetails` | Add `basic_pay`, `deductions`, `taxable_pay`, `tax`, `net_pay` |
| UC10 | `feature/UC10-erDiagram` | Identify redundancy; design ER diagram |
| UC11 | `feature/UC11-implementERDiagram` | Implement normalized tables from ER diagram |
| UC12 | `feature/UC12-validateQueriesNewStructure` | Validate UC4, UC5, UC7 on new structure |

### Section 3 — JDBC

| UC  | Branch | Description |
|-----|--------|-------------|
| UC1 | `feature/JDBC-UC1-dbConnection` | Connect Java app to `payroll_service` DB |
| UC2 | `feature/JDBC-UC2-retrieveEmployeePayroll` | Retrieve all payroll records via JDBC |
| UC3 | `feature/JDBC-UC3-updateSalaryStatement` | Update Terisa salary using `Statement` |
| UC4 | `feature/JDBC-UC4-preparedStatementRefactor` | Refactor with `PreparedStatement` + Singleton + caching |
| UC5 | `feature/JDBC-UC5-dateRangeQuery` | Retrieve employees by date range |
| UC6 | `feature/JDBC-UC6-genderAnalysis` | SUM, AVG, MIN, MAX, COUNT grouped by gender |

---

## JDBC Architecture

```
Java App
   │
   ├── DriverManager.getConnection(url, user, password)
   │         └── Connection
   │               ├── createStatement()       → Statement        (UC3)
   │               └── prepareStatement(sql)   → PreparedStatement (UC4, cached)
   │
   └── ResultSet ← executeQuery() / executeUpdate()
                       └── mapResultSetToList()  (reusable helper — Refactor UC4)
```

**Key Classes:**

| Class | Role |
|-------|------|
| `EmployeePayrollDBService` | Singleton — all JDBC operations |
| `EmployeePayrollService` | Business logic layer — delegates to DBService |
| `EmployeePayrollData` | POJO — holds employee payroll record |
| `EmployeePayrollException` | Custom exception with typed errors |

**Connection String:**
```
jdbc:mysql://localhost:3306/payroll_service?useSSL=false&allowPublicKeyRetrieval=true
```

---

## Getting Started

### Prerequisites

- MySQL 8.0+ installed and running
- Java JDK 25
- IntelliJ IDEA
- Maven

### Steps

**1. Clone the repository**
```bash
git clone https://github.com/barnamwork/PayrollService_JDBC.git
cd PayrollService_JDBC
```

**2. Run the DB setup scripts in order**
```bash
# In MySQL client or MySQL Workbench
source sql/UC1.sql
source sql/UC2.sql
source sql/UC3.sql
source sql/UC11.sql
```

**3. Update DB credentials in `EmployeePayrollDBService.java`**
```java
private static final String PASSWORD = "your_mysql_password";
```

**4. Open in IntelliJ IDEA as a Maven project and run**
```
Right-click EmployeePayrollService.java → Run
```

---

## Project Structure

```
PayrollService-JDBC/
├── pom.xml
├── sql/
│   ├── UC1.sql       ← Create database
│   ├── UC2.sql       ← Create table
│   ├── UC3.sql       ← Insert data
│   ├── UC4.sql       ← Retrieve all
│   ├── UC5.sql       ← Date range query
│   ├── UC6.sql       ← Add gender
│   ├── UC7.sql       ← Aggregation
│   ├── UC8.sql       ← Extend table
│   ├── UC9.sql       ← Payroll details
│   ├── UC10.sql      ← Redundancy demo
│   ├── UC11.sql      ← Normalized tables
│   └── UC12.sql      ← Validate queries
└── src/
    └── main/java/jdbc/
        ├── EmployeePayrollData.java
        ├── EmployeePayrollDBService.java
        ├── EmployeePayrollService.java
        └── EmployeePayrollException.java
```

---

## Git Flow

Feature branches are kept alive after merging into `develop`.

```
main
 └── develop
      ├── feature/UC1-createDatabase                    ← kept ✅
      ├── feature/UC2-createEmployeePayrollTable         ← kept ✅
      ├── feature/UC3-insertEmployeePayrollData          ← kept ✅
      ├── feature/UC4-retrieveAllEmployeePayroll         ← kept ✅
      ├── feature/UC5-retrieveSalaryByNameAndDateRange   ← kept ✅
      ├── feature/UC6-addGenderAndUpdate                 ← kept ✅
      ├── feature/UC7-genderBasedAggregation             ← kept ✅
      ├── feature/UC8-extendEmployeePayrollData          ← kept ✅
      ├── feature/UC9-extendPayrollDetails               ← kept ✅
      ├── feature/UC10-erDiagram                        ← kept ✅
      ├── feature/UC11-implementERDiagram               ← kept ✅
      ├── feature/UC12-validateQueriesNewStructure       ← kept ✅
      ├── feature/JDBC-UC1-dbConnection                 ← kept ✅
      ├── feature/JDBC-UC2-retrieveEmployeePayroll       ← kept ✅
      ├── feature/JDBC-UC3-updateSalaryStatement         ← kept ✅
      ├── feature/JDBC-UC4-preparedStatementRefactor     ← kept ✅
      ├── feature/JDBC-UC5-dateRangeQuery               ← kept ✅
      └── feature/JDBC-UC6-genderAnalysis               ← kept ✅
```

### Branch Commands
```bash
git flow feature start <feature-name>
git add .
git commit -m "[Barnam]: <message>"
git push origin head
git flow feature finish -k <feature-name>
git push origin head
```

---

## Sample Queries

```sql
-- Retrieve Bill's salary
SELECT basic_pay FROM employee_payroll WHERE name = 'Bill';

-- Employees joined between two dates
SELECT * FROM employee_payroll
WHERE start BETWEEN CAST('2018-01-01' AS DATE) AND DATE(NOW());

-- Gender aggregation
SELECT gender,
       SUM(basic_pay)  AS total_salary,
       AVG(basic_pay)  AS avg_salary,
       MIN(basic_pay)  AS min_salary,
       MAX(basic_pay)  AS max_salary,
       COUNT(*)        AS employee_count
FROM employee_payroll
GROUP BY gender;
```
