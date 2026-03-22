package jdbc;

public class EmployeePayrollException extends Exception {

    public enum ExceptionType {
        DATABASE_EXCEPTION,
        NO_DATA_FOUND,
        UPDATE_FAILED
    }

    public final ExceptionType type;

    public EmployeePayrollException(ExceptionType type, String message) {
        super(message);
        this.type = type;
    }

    public EmployeePayrollException(ExceptionType type, String message, Throwable cause) {
        super(message, cause);
        this.type = type;
    }
}