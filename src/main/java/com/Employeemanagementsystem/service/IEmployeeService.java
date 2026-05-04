package com.Employeemanagementsystem.service;

import com.Employeemanagementsystem.model.Employee;

import java.sql.SQLException;
import java.util.List;

public interface IEmployeeService {
    Employee createEmployeeWithUser(Employee e, String plainPassword) throws SQLException;
    boolean updateEmployee(Employee e) throws SQLException;
    boolean deleteEmployee(int id) throws SQLException;
    Employee getEmployeeById(int id) throws SQLException;
    Employee getEmployeeByEmail(String email) throws SQLException;
    List<Employee> listEmployees(String search, String department, String status) throws SQLException;
}
