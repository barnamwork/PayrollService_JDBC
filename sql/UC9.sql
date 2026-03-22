-- UC9: Add basic_pay deductions taxable_pay tax net_pay
USE payroll_service;

ALTER TABLE employee_payroll
    ADD basic_pay   DOUBLE NOT NULL AFTER department,
    ADD deductions  DOUBLE NOT NULL AFTER basic_pay,
    ADD taxable_pay DOUBLE NOT NULL AFTER deductions,
    ADD tax         DOUBLE NOT NULL AFTER taxable_pay,
    ADD net_pay     DOUBLE NOT NULL AFTER tax;

DESCRIBE employee_payroll;

-- Insert Terisa with full payroll details for both departments (UC10 problem)
INSERT INTO employee_payroll
(name, gender, department, basic_pay, deductions, taxable_pay, tax, net_pay, salary, start)
VALUES
    ('Terisa', 'F', 'Sales',      3000000, 1000000, 2000000, 500000, 1500000, 3000000, '2019-11-13'),
    ('Terisa', 'F', 'Marketing',  3000000, 1000000, 2000000, 500000, 1500000, 3000000, '2018-01-03');

SELECT * FROM employee_payroll WHERE name = 'Terisa';