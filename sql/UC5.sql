-- UC5: Retrieve salary by name and filter by date range
USE payroll_service;

-- Retrieve salary for Bill
SELECT salary FROM employee_payroll WHERE name = 'Bill';

-- Retrieve employees who joined between dates
SELECT * FROM employee_payroll
WHERE start BETWEEN CAST('2018-01-01' AS DATE) AND DATE(NOW());