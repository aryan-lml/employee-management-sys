package model;

public class Employee {
    private int id;
    private String employeeCode;
    private String name;
    private String email;
    private String department;
    private boolean status; // true = active

    public Employee(){}

    public Employee(int id, String employeeCode, String name, String email, String department, boolean status) {
        this.id = id;
        this.employeeCode = employeeCode;
        this.name = name;
        this.email = email;
        this.department = department;
        this.status = status;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getEmployeeCode() { return employeeCode; }
    public void setEmployeeCode(String employeeCode) { this.employeeCode = employeeCode; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public boolean isStatus() { return status; }
    public void setStatus(boolean status) { this.status = status; }
}
