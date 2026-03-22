package jdbc;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class EmployeePayrollService {

    private List<EmployeePayrollData> employeePayrollList;
    private final EmployeePayrollDBService dbService;

    public EmployeePayrollService() {
        dbService = EmployeePayrollDBService.getInstance();
    }

    public static void main(String[] args) throws EmployeePayrollException {
        EmployeePayrollService service = new EmployeePayrollService();

        // ── UC2 ─────────────────────────────────────────────
        System.out.println("\n=== UC2: All Employee Payroll Data ===");
        List<EmployeePayrollData> list = service.readEmployeePayrollData();
        list.forEach(System.out::println);
        System.out.println("Total Employees: " + list.size());

        // ── UC3 ─────────────────────────────────────────────
        System.out.println("\n=== UC3: Update Terisa Salary via Statement ===");
        service.updateEmployeeSalary("Terisa", 3000000.00);
        System.out.println("Updated Terisa salary to 3000000.00");
        System.out.println("Terisa in sync with DB: " +
                service.checkEmployeePayrollInSyncWithDB("Terisa"));

        // ── UC4 ─────────────────────────────────────────────
        System.out.println("\n=== UC4: Update Terisa Salary via PreparedStatement ===");
        service.updateEmployeeSalaryWithPreparedStatement("Terisa", 3500000.00);
        System.out.println("Updated Terisa salary to 3500000.00");
        System.out.println("Terisa in sync with DB: " +
                service.checkEmployeePayrollInSyncWithDB("Terisa"));

        // ── UC5 ─────────────────────────────────────────────
        System.out.println("\n=== UC5: Employees who joined between 2018-01-01 and today ===");
        List<EmployeePayrollData> rangeList = service.readEmployeePayrollDataForDateRange(
                LocalDate.of(2018, 1, 1), LocalDate.now());
        rangeList.forEach(System.out::println);
        System.out.println("Total Employees in range: " + rangeList.size());

        // ── UC6 ─────────────────────────────────────────────
        System.out.println("\n=== UC6: Average Salary by Gender ===");
        service.readAvgSalaryByGender().forEach((gender, avg) ->
                System.out.println(gender + " : " + avg));

        System.out.println("\n=== UC6: Sum Salary by Gender ===");
        service.readSumSalaryByGender().forEach((gender, sum) ->
                System.out.println(gender + " : " + sum));

        System.out.println("\n=== UC6: Min Salary by Gender ===");
        service.readMinSalaryByGender().forEach((gender, min) ->
                System.out.println(gender + " : " + min));

        System.out.println("\n=== UC6: Max Salary by Gender ===");
        service.readMaxSalaryByGender().forEach((gender, max) ->
                System.out.println(gender + " : " + max));

        System.out.println("\n=== UC6: Count by Gender ===");
        service.readCountByGender().forEach((gender, count) ->
                System.out.println(gender + " : " + count));
    }

    // ── UC2 ─────────────────────────────────────────────────
    public List<EmployeePayrollData> readEmployeePayrollData() throws EmployeePayrollException {
        employeePayrollList = dbService.getEmployeePayrollData();
        return employeePayrollList;
    }

    // ── UC3 ─────────────────────────────────────────────────
    public void updateEmployeeSalary(String name, double salary) throws EmployeePayrollException {
        int rows = dbService.updateEmployeeSalary(name, salary);
        if (rows == 0)
            throw new EmployeePayrollException(
                    EmployeePayrollException.ExceptionType.NO_DATA_FOUND,
                    "Employee not found: " + name);
        EmployeePayrollData emp = findByName(name);
        if (emp != null) emp.salary = salary;
    }

    // ── UC4 ─────────────────────────────────────────────────
    public void updateEmployeeSalaryWithPreparedStatement(String name, double salary)
            throws EmployeePayrollException {
        int rows = dbService.updateEmployeeSalaryUsingPreparedStatement(name, salary);
        if (rows == 0)
            throw new EmployeePayrollException(
                    EmployeePayrollException.ExceptionType.NO_DATA_FOUND,
                    "Employee not found: " + name);
        List<EmployeePayrollData> updated = dbService.getEmployeePayrollData(name);
        if (updated != null && !updated.isEmpty())
            syncInMemoryList(updated.get(0));
    }

    public boolean checkEmployeePayrollInSyncWithDB(String name) throws EmployeePayrollException {
        List<EmployeePayrollData> dbList = dbService.getEmployeePayrollData(name);
        EmployeePayrollData inMemory = findByName(name);
        if (dbList == null || dbList.isEmpty() || inMemory == null) return false;
        return Double.compare(dbList.get(0).salary, inMemory.salary) == 0;
    }

    // ── UC5 ─────────────────────────────────────────────────
    public List<EmployeePayrollData> readEmployeePayrollDataForDateRange(
            LocalDate start, LocalDate end) throws EmployeePayrollException {
        return dbService.getEmployeePayrollDataForDateRange(start, end);
    }

    // ── UC6 ─────────────────────────────────────────────────
    public Map<String, Double> readAvgSalaryByGender() throws EmployeePayrollException {
        return dbService.getAvgSalaryByGender();
    }

    public Map<String, Double> readSumSalaryByGender() throws EmployeePayrollException {
        return dbService.getSumSalaryByGender();
    }

    public Map<String, Double> readMinSalaryByGender() throws EmployeePayrollException {
        return dbService.getMinSalaryByGender();
    }

    public Map<String, Double> readMaxSalaryByGender() throws EmployeePayrollException {
        return dbService.getMaxSalaryByGender();
    }

    public Map<String, Long> readCountByGender() throws EmployeePayrollException {
        return dbService.getCountByGender();
    }

    // ── Helpers ─────────────────────────────────────────────
    private EmployeePayrollData findByName(String name) {
        if (employeePayrollList == null) return null;
        return employeePayrollList.stream()
                .filter(e -> e.name.equalsIgnoreCase(name))
                .findFirst().orElse(null);
    }

    private void syncInMemoryList(EmployeePayrollData updated) {
        if (employeePayrollList == null) return;
        for (int i = 0; i < employeePayrollList.size(); i++) {
            if (employeePayrollList.get(i).name.equalsIgnoreCase(updated.name)) {
                employeePayrollList.set(i, updated);
                return;
            }
        }
    }
}