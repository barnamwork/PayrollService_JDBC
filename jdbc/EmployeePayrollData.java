package jdbc;

import java.time.LocalDate;

public class EmployeePayrollData {

    public int id;
    public String name;
    public String gender;
    public double salary;
    public LocalDate startDate;

    public EmployeePayrollData(int id, String name, double salary) {
        this.id = id;
        this.name = name;
        this.salary = salary;
    }

    public EmployeePayrollData(int id, String name, double salary, LocalDate startDate) {
        this(id, name, salary);
        this.startDate = startDate;
    }

    public EmployeePayrollData(int id, String name, String gender, double salary, LocalDate startDate) {
        this(id, name, salary, startDate);
        this.gender = gender;
    }

    @Override
    public String toString() {
        return "EmployeePayrollData{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", gender='" + gender + '\'' +
                ", salary=" + salary +
                ", startDate=" + startDate +
                '}';
    }
}