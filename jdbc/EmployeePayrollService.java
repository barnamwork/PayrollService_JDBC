package jdbc;

import java.util.List;

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

    public boolean checkEmployeePayrollInSyncWithDB(String name) throws EmployeePayrollException {
        List<EmployeePayrollData> dbList = dbService.getEmployeePayrollData();
        EmployeePayrollData inMemory = findByName(name);
        if (dbList == null || dbList.isEmpty() || inMemory == null) return false;
        return dbList.stream()
                .filter(e -> e.name.equalsIgnoreCase(name))
                .findFirst()
                .map(e -> Double.compare(e.salary, inMemory.salary) == 0)
                .orElse(false);
    }

    // ── Helper ───────────────────────────────────────────────
    private EmployeePayrollData findByName(String name) {
        if (employeePayrollList == null) return null;
        return employeePayrollList.stream()
                .filter(e -> e.name.equalsIgnoreCase(name))
                .findFirst().orElse(null);
    }
}
