package jdbc;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmployeePayrollDBService {

    private static final String JDBC_URL =
            "jdbc:mysql://localhost:3306/payroll_service?useSSL=false&allowPublicKeyRetrieval=true";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "barnam@555";

    private static EmployeePayrollDBService instance;
    private Connection connection;

    private PreparedStatement getEmployeePayrollDataStatement;
    private PreparedStatement updateSalaryStatement;

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

    // ── UC4: Retrieve by name via cached PreparedStatement ───
    public List<EmployeePayrollData> getEmployeePayrollData(String name)
            throws EmployeePayrollException {
        if (getEmployeePayrollDataStatement == null)
            prepareStatementForEmployeeData();
        try {
            getEmployeePayrollDataStatement.setString(1, name);
            ResultSet rs = getEmployeePayrollDataStatement.executeQuery();
            return mapResultSetToList(rs);
        } catch (SQLException e) {
            throw new EmployeePayrollException(
                    EmployeePayrollException.ExceptionType.DATABASE_EXCEPTION,
                    "Error fetching data for: " + name, e);
        }
    }

    private void prepareStatementForEmployeeData() throws EmployeePayrollException {
        try {
            String sql = "SELECT * FROM employee_payroll WHERE name = ?;";
            getEmployeePayrollDataStatement = getConnection().prepareStatement(sql);
        } catch (SQLException e) {
            throw new EmployeePayrollException(
                    EmployeePayrollException.ExceptionType.DATABASE_EXCEPTION,
                    "Error preparing select statement", e);
        }
    }

    // ── UC4: Update salary via cached PreparedStatement ──────
    public int updateEmployeeSalaryUsingPreparedStatement(String name, double salary)
            throws EmployeePayrollException {
        if (updateSalaryStatement == null)
            prepareStatementForSalaryUpdate();
        try {
            updateSalaryStatement.setDouble(1, salary);
            updateSalaryStatement.setString(2, name);
            return updateSalaryStatement.executeUpdate();
        } catch (SQLException e) {
            throw new EmployeePayrollException(
                    EmployeePayrollException.ExceptionType.UPDATE_FAILED,
                    "Error updating salary (PreparedStatement) for: " + name, e);
        }
    }

    private void prepareStatementForSalaryUpdate() throws EmployeePayrollException {
        try {
            String sql = "UPDATE employee_payroll SET basic_pay = ? WHERE name = ?;";
            updateSalaryStatement = getConnection().prepareStatement(sql);
        } catch (SQLException e) {
            throw new EmployeePayrollException(
                    EmployeePayrollException.ExceptionType.DATABASE_EXCEPTION,
                    "Error preparing update statement", e);
        }
    }

    // ── UC5: Retrieve by date range ──────────────────────────
    public List<EmployeePayrollData> getEmployeePayrollDataForDateRange(
            LocalDate startDate, LocalDate endDate) throws EmployeePayrollException {
        String sql = String.format(
                "SELECT * FROM employee_payroll WHERE start BETWEEN CAST('%s' AS DATE) AND CAST('%s' AS DATE);",
                startDate, endDate);
        return executeSelectQuery(sql);
    }

    // ── UC6: Aggregation by gender ───────────────────────────
    public Map<String, Double> getAvgSalaryByGender() throws EmployeePayrollException {
        return getGenderDoubleResult(
                "SELECT gender, AVG(basic_pay) AS avg_salary FROM employee_payroll GROUP BY gender;",
                "avg_salary");
    }

    public Map<String, Double> getSumSalaryByGender() throws EmployeePayrollException {
        return getGenderDoubleResult(
                "SELECT gender, SUM(basic_pay) AS sum_salary FROM employee_payroll GROUP BY gender;",
                "sum_salary");
    }

    public Map<String, Double> getMinSalaryByGender() throws EmployeePayrollException {
        return getGenderDoubleResult(
                "SELECT gender, MIN(basic_pay) AS min_salary FROM employee_payroll GROUP BY gender;",
                "min_salary");
    }

    public Map<String, Double> getMaxSalaryByGender() throws EmployeePayrollException {
        return getGenderDoubleResult(
                "SELECT gender, MAX(basic_pay) AS max_salary FROM employee_payroll GROUP BY gender;",
                "max_salary");
    }

    public Map<String, Long> getCountByGender() throws EmployeePayrollException {
        Map<String, Long> result = new HashMap<>();
        String sql = "SELECT gender, COUNT(*) AS emp_count FROM employee_payroll GROUP BY gender;";
        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next())
                result.put(rs.getString("gender"), rs.getLong("emp_count"));
        } catch (SQLException e) {
            throw new EmployeePayrollException(
                    EmployeePayrollException.ExceptionType.DATABASE_EXCEPTION,
                    "Error fetching count by gender", e);
        }
        return result;
    }

    private Map<String, Double> getGenderDoubleResult(String sql, String column)
            throws EmployeePayrollException {
        Map<String, Double> result = new HashMap<>();
        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next())
                result.put(rs.getString("gender"), rs.getDouble(column));
        } catch (SQLException e) {
            throw new EmployeePayrollException(
                    EmployeePayrollException.ExceptionType.DATABASE_EXCEPTION,
                    "Error executing aggregate query", e);
        }
        return result;
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