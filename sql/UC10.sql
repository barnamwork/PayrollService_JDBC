-- UC10: Show redundancy problem — Terisa appears twice with 2 different IDs
USE payroll_service;

SELECT * FROM employee_payroll WHERE name = 'Terisa';

-- Problem 1: Two rows for same employee = two different employee IDs
-- Problem 2: Updating salary requires updating multiple rows
-- Problem 3: Department stored in employee table causes data duplication
-- Solution:  Normalize using ER Diagram