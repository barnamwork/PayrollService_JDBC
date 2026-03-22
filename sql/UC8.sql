-- UC8: Extend table with phone, address, department
USE payroll_service;

ALTER TABLE employee_payroll
    ADD phone_number VARCHAR(250) AFTER gender,
    ADD address      VARCHAR(250) DEFAULT 'TBD' AFTER phone_number,
    ADD department   VARCHAR(150) NOT NULL AFTER address;

DESCRIBE employee_payroll;