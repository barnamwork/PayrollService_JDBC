package jdbc;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    // ── UC2: Retrieve all ────────────────────────────────────
    public List<EmployeePayrollData> getEmployeePayrollData() throws EmployeePayrollException {
        String sql = "SELECT * FROM employee_payroll;";
        return executeSelectQuery(sql);
    }

    // ── UC3: Update salary via Statement ────────────────────
    public int updateEmployeeSalary(String name, double salary) throws EmployeePayrollException {
        String sql = String.format(
                "UPDATE employee_payroll SET basic_pay = %.2f WHERE name = '%s';", salary, name);
        try (Statement stmt = getConnection().createStatement()) {
            return stmt.executeUpdate(sql);
        } catch (SQLException e) {
            throw new EmployeePayrollException(
                    EmployeePayrollException.ExceptionType.UPDATE_FAILED,
                    "Error updating salary for: " + name, e);
        }
    }

    // ── Helpers ──────────────────────────────────────────────
    private List<EmployeePayrollData> executeSelectQuery(String sql) throws EmployeePayrollException {
        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return mapResultSetToList(rs);
        } catch (SQLException e) {
            throw new EmployeePayrollException(
                    EmployeePayrollException.ExceptionType.DATABASE_EXCEPTION,
                    "Query failed: " + sql, e);
        }
    }

    private List<EmployeePayrollData> mapResultSetToList(ResultSet rs) throws SQLException {
        List<EmployeePayrollData> list = new ArrayList<>();
        while (rs.next()) {
            int id          = rs.getInt("id");
            String name     = rs.getString("name");
            String gender   = rs.getString("gender");
            double salary   = rs.getDouble("basic_pay");
            LocalDate start = rs.getDate("start").toLocalDate();
            list.add(new EmployeePayrollData(id, name, gender, salary, start));
        }
        return list;
    }
}