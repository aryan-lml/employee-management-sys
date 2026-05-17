package com.Employeemanagementsystem.service;

import com.Employeemanagementsystem.model.Employee;

import java.sql.SQLException;
import java.util.List;

/**
 * Business operations for employees, scoped by the owning admin.
 */
public interface IEmployeeService {
    Employee createEmployeeWithUser(Employee e, String plainPassword) throws SQLException;
    boolean updateEmployee(Employee e) throws SQLException;
    boolean deleteEmployee(int id, Integer ownerAdminId) throws SQLException;
    Employee getEmployeeById(int id, Integer ownerAdminId) throws SQLException;
    Employee getEmployeeByEmail(String email) throws SQLException;
    List<Employee> listEmployees(Integer ownerAdminId, String search, String department, String status) throws SQLException;
}
