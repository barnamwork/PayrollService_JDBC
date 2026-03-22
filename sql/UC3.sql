-- UC3: Insert employee payroll data
USE payroll_service;

INSERT INTO employee_payroll (name, salary, start) VALUES
                                                       ('Bill',    1000000.00, '2018-01-03'),
                                                       ('Mark',    2000000.00, '2019-11-13'),
                                                       ('Charlie', 3000000.00, '2020-05-21');

SELECT * FROM employee_payroll;