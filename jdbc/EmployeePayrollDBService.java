package jdbc;

import java.sql.*;

public class EmployeePayrollDBService {

    private static final String JDBC_URL =
            "jdbc:mysql://localhost:3306/payroll_service?useSSL=false&allowPublicKeyRetrieval=true";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "barnam@555";

    private static EmployeePayrollDBService instance;
    private Connection connection;

    private EmployeePayrollDBService() {}

    public static EmployeePayrollDBService getInstance() {
        if (instance == null)
            instance = new EmployeePayrollDBService();
        return instance;
    }

    private Connection getConnection() throws EmployeePayrollException {
        try {
            if (connection == null || connection.isClosed()) {
                System.out.println("Connecting to: " + JDBC_URL);
                connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
                System.out.println("Connection established: " + connection);
            }
            return connection;
        } catch (SQLException e) {
            throw new EmployeePayrollException(
                    EmployeePayrollException.ExceptionType.DATABASE_EXCEPTION,
                    "Unable to connect: " + e.getMessage(), e);
        }
    }

    // ── UC1: main — test DB connection ──────────────────────
    public static void main(String[] args) {
        try {
            EmployeePayrollDBService dbService = EmployeePayrollDBService.getInstance();
            dbService.getConnection();
            System.out.println("=== UC1: Connection to payroll_service DB successful ===");
        } catch (EmployeePayrollException e) {
            System.out.println("Connection failed: " + e.getMessage());
        }
    }
}