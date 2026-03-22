-- UC7: Sum average min max count by gender
USE payroll_service;

SELECT gender,
       COUNT(*)    AS employee_count,
       SUM(salary) AS total_salary,
       AVG(salary) AS avg_salary,
       MIN(salary) AS min_salary,
       MAX(salary) AS max_salary
FROM employee_payroll
GROUP BY gender;
