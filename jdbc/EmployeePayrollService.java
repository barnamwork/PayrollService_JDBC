package jdbc;

import java.util.List;

public class EmployeePayrollService {

    private List<EmployeePayrollData> employeePayrollList;
    private final EmployeePayrollDBService dbService;

    public EmployeePayrollService() {
        dbService = EmployeePayrollDBService.getInstance();
    }

    // ── UC2: Retrieve all ────────────────────────────────────
    public List<EmployeePayrollData> readEmployeePayrollData() throws EmployeePayrollException {
        employeePayrollList = dbService.getEmployeePayrollData();
        return employeePayrollList;
    }

    public static void main(String[] args) throws EmployeePayrollException {
        EmployeePayrollService service = new EmployeePayrollService();

        // ── UC2 ─────────────────────────────────────────────
        System.out.println("\n=== UC2: All Employee Payroll Data ===");
        List<EmployeePayrollData> list = service.readEmployeePayrollData();
        list.forEach(System.out::println);
        System.out.println("Total Employees: " + list.size());
    }
}