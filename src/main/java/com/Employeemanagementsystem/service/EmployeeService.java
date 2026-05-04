package com.Employeemanagementsystem.service;

import com.Employeemanagementsystem.config.DBConfig;
import com.Employeemanagementsystem.dao.EmployeeDao;
import com.Employeemanagementsystem.dao.UserDao;
import com.Employeemanagementsystem.model.Employee;
import com.Employeemanagementsystem.model.User;
import com.Employeemanagementsystem.util.PasswordUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class EmployeeService implements IEmployeeService {

    private final EmployeeDao employeeDao = new EmployeeDao();
    private final UserDao userDao = new UserDao();

    /**
     * Create employee and associated user in a single transaction. Username will be employee.email
     */
    @Override
    public Employee createEmployeeWithUser(Employee e, String plainPassword) throws SQLException {
        Connection conn = null;
        try {
            conn = DBConfig.getConnection();
            conn.setAutoCommit(false);

            // If password is provided, create user account
            if (plainPassword != null && !plainPassword.isBlank()) {
                // ensure username doesn't exist
                User existing = userDao.findByUsername(conn, e.getEmail());
                if (existing != null) {
                    throw new SQLException("User with username already exists: " + e.getEmail());
                }
                String hashed = PasswordUtils.hashPassword(plainPassword);
                boolean created = userDao.createUserWithHash(conn, e.getEmail(), hashed, "EMPLOYEE");
                if (!created) throw new SQLException("Failed to create user account");
                User createdUser = userDao.findByUsername(conn, e.getEmail());
                if (createdUser == null) throw new SQLException("User created but not found");
                e.setUserId(createdUser.getId());
            }

            boolean added = employeeDao.addEmployee(conn, e);
            if (!added) throw new SQLException("Failed to insert employee record");

            conn.commit();
            return e;
        } catch (SQLException ex) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ignore) {}
            throw ex;
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ignore) {}
        }
    }

    @Override
    public boolean updateEmployee(Employee e) throws SQLException {
        return employeeDao.updateEmployee(e);
    }

    @Override
    public boolean deleteEmployee(int id) throws SQLException {
        return employeeDao.deleteEmployee(id);
    }

    @Override
    public Employee getEmployeeById(int id) throws SQLException {
        return employeeDao.getEmployeeById(id);
    }

    @Override
    public Employee getEmployeeByEmail(String email) throws SQLException {
        return employeeDao.getEmployeeByEmail(email);
    }

    @Override
    public List<Employee> listEmployees(String search, String department, String status) throws SQLException {
        return employeeDao.getAllEmployees(search, department, status);
    }
}
