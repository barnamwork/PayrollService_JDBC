-- UC6: Add gender column and update rows
USE payroll_service;

ALTER TABLE employee_payroll
    ADD gender CHAR(1) AFTER name;

UPDATE employee_payroll SET gender = 'M'
WHERE name = 'Bill' OR name = 'Charlie' OR name = 'Mark';

SELECT * FROM employee_payroll;