-- UC12: Validate UC4, UC5, UC7 queries work with new normalized structure
USE payroll_service;

-- UC4 equivalent: Retrieve all employees with payroll
SELECT e.employee_id, e.name, e.gender, p.basic_pay, e.start
FROM employee e
         JOIN payroll p ON e.employee_id = p.employee_id;

-- UC5 equivalent: Retrieve employees by date range
SELECT e.employee_id, e.name, p.basic_pay, e.start
FROM employee e
         JOIN payroll p ON e.employee_id = p.employee_id
WHERE e.start BETWEEN CAST('2018-01-01' AS DATE) AND DATE(NOW());

-- UC7 equivalent: Aggregation by gender
SELECT e.gender,
       COUNT(*)        AS employee_count,
       SUM(p.basic_pay) AS total_salary,
       AVG(p.basic_pay) AS avg_salary,
       MIN(p.basic_pay) AS min_salary,
       MAX(p.basic_pay) AS max_salary
FROM employee e
         JOIN payroll p ON e.employee_id = p.employee_id
GROUP BY e.gender;