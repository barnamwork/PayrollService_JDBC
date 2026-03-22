-- UC2: Create employee_payroll table
USE payroll_service;

CREATE TABLE employee_payroll (
                                  id     INT unsigned NOT NULL AUTO_INCREMENT,
                                  name   VARCHAR(150) NOT NULL,
                                  salary DOUBLE NOT NULL,
                                  start  DATE NOT NULL,
                                  PRIMARY KEY (id)
);

DESCRIBE employee_payroll;