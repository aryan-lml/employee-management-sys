package com.Employeemanagementsystem.dao;

import com.Employeemanagementsystem.model.Employee;

import java.sql.SQLException;
import java.util.List;

/**
 * Read/write contract for the employees table.
 * Most queries are scoped by ownerAdminId so each admin only sees their own roster.
 */
public interface IEmployeeDao {
    List<Employee> getAllEmployees(Integer ownerAdminId, String search, String department, String status) throws SQLException;
    Employee getEmployeeById(int id, Integer ownerAdminId) throws SQLException;
    Employee getEmployeeByEmail(String email) throws SQLException;
    Employee getEmployeeByUserId(int userId) throws SQLException;
    boolean addEmployee(Employee e) throws SQLException;
    boolean updateEmployee(Employee e) throws SQLException;
    boolean deleteEmployee(int id, Integer ownerAdminId) throws SQLException;
}
