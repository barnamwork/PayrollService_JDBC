-- UC11: Implement ER Diagram — normalized tables
USE payroll_service;

DROP TABLE IF EXISTS employee_department;
DROP TABLE IF EXISTS payroll;
DROP TABLE IF EXISTS employee;
DROP TABLE IF EXISTS department;

-- Department table
CREATE TABLE department (
                            department_id   INT unsigned NOT NULL AUTO_INCREMENT,
                            department_name VARCHAR(150) NOT NULL,
                            PRIMARY KEY (department_id)
);

-- Employee table
CREATE TABLE employee (
                          employee_id  INT unsigned NOT NULL AUTO_INCREMENT,
                          name         VARCHAR(150) NOT NULL,
                          gender       CHAR(1),
                          phone_number VARCHAR(250),
                          address      VARCHAR(250) DEFAULT 'TBD',
                          start        DATE NOT NULL,
                          PRIMARY KEY (employee_id)
);

-- Payroll table — One to One with Employee
CREATE TABLE payroll (
                         payroll_id  INT unsigned NOT NULL AUTO_INCREMENT,
                         employee_id INT unsigned NOT NULL,
                         basic_pay   DOUBLE NOT NULL,
                         deductions  DOUBLE NOT NULL,
                         taxable_pay DOUBLE NOT NULL,
                         tax         DOUBLE NOT NULL,
                         net_pay     DOUBLE NOT NULL,
                         PRIMARY KEY (payroll_id),
                         FOREIGN KEY (employee_id) REFERENCES employee(employee_id)
);

-- Employee_Department junction table — Many to Many
CREATE TABLE employee_department (
                                     employee_id   INT unsigned NOT NULL,
                                     department_id INT unsigned NOT NULL,
                                     PRIMARY KEY (employee_id, department_id),
                                     FOREIGN KEY (employee_id)   REFERENCES employee(employee_id),    -- ← fixed
                                     FOREIGN KEY (department_id) REFERENCES department(department_id)
);

-- Insert departments
INSERT INTO department (department_name) VALUES
                                             ('Engineering'), ('Sales'), ('Marketing'), ('HR');

-- Insert employees (Terisa only once)
INSERT INTO employee (name, gender, start) VALUES
                                               ('Bill',    'M', '2018-01-03'),
                                               ('Terisa',  'F', '2018-01-03'),
                                               ('Mark',    'M', '2019-11-13'),
                                               ('Charlie', 'M', '2020-05-21');

-- Insert payroll
INSERT INTO payroll (employee_id, basic_pay, deductions, taxable_pay, tax, net_pay) VALUES
                                                                                        (1, 1000000, 0,       0,       0,      0),
                                                                                        (2, 3000000, 1000000, 2000000, 500000, 1500000),
                                                                                        (3, 2000000, 0,       0,       0,      0),
                                                                                        (4, 3000000, 0,       0,       0,      0);

-- Insert employee_department (Terisa in both Sales and Marketing)
INSERT INTO employee_department (employee_id, department_id) VALUES
                                                                 (1, 1),   -- Bill    → Engineering
                                                                 (2, 2),   -- Terisa  → Sales
                                                                 (2, 3),   -- Terisa  → Marketing
                                                                 (3, 4),   -- Mark    → HR
                                                                 (4, 3);   -- Charlie → Marketing

SELECT * FROM employee;
SELECT * FROM department;
SELECT * FROM payroll;
SELECT * FROM employee_department;