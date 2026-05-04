package com.Employeemanagementsystem.dao;

import com.Employeemanagementsystem.model.Employee;

import java.sql.SQLException;
import java.util.List;

public interface IEmployeeDao {
    List<Employee> getAllEmployees(String search, String department, String status) throws SQLException;
    Employee getEmployeeById(int id) throws SQLException;
    Employee getEmployeeByEmail(String email) throws SQLException;
    boolean addEmployee(Employee e) throws SQLException;
    boolean updateEmployee(Employee e) throws SQLException;
    boolean deleteEmployee(int id) throws SQLException;
}
