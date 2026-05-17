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

/**
 * Service layer for employee operations.
 *
 * The main value-add is {@link #createEmployeeWithUser} which atomically
 * creates an employee row and (optionally) a USER login in a single
 * transaction so we never end up with an orphan account.
 */
public class EmployeeService implements IEmployeeService {

    private final EmployeeDao employeeDao = new EmployeeDao();
    private final UserDao userDao = new UserDao();

    @Override
    public Employee createEmployeeWithUser(Employee e, String plainPassword) throws SQLException {
        Connection conn = null;
        try {
            conn = DBConfig.getConnection();
            conn.setAutoCommit(false);

            if (plainPassword != null && !plainPassword.isBlank()) {
                User existing = userDao.findByUsername(conn, e.getEmail());
                if (existing != null) throw new SQLException("User already exists: " + e.getEmail());
                String hashed = PasswordUtils.hashPassword(plainPassword);
                if (!userDao.createUserWithHash(conn, e.getEmail(), hashed, "USER"))
                    throw new SQLException("Failed to create user account");
                User createdUser = userDao.findByUsername(conn, e.getEmail());
                if (createdUser == null) throw new SQLException("User created but not found");
                e.setUserId(createdUser.getId());
            }
            if (!employeeDao.addEmployee(conn, e)) throw new SQLException("Failed to insert employee record");

            conn.commit();
            return e;
        } catch (SQLException ex) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ignore) {}
            throw ex;
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ignore) {}
        }
    }

    @Override public boolean updateEmployee(Employee e) throws SQLException { return employeeDao.updateEmployee(e); }
    @Override public boolean deleteEmployee(int id, Integer ownerAdminId) throws SQLException { return employeeDao.deleteEmployee(id, ownerAdminId); }
    @Override public Employee getEmployeeById(int id, Integer ownerAdminId) throws SQLException { return employeeDao.getEmployeeById(id, ownerAdminId); }
    @Override public Employee getEmployeeByEmail(String email) throws SQLException { return employeeDao.getEmployeeByEmail(email); }

    @Override
    public List<Employee> listEmployees(Integer ownerAdminId, String search, String department, String status) throws SQLException {
        return employeeDao.getAllEmployees(ownerAdminId, search, department, status);
    }
}
