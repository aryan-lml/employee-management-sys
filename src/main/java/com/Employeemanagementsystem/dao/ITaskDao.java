package com.Employeemanagementsystem.dao;

import com.Employeemanagementsystem.model.Task;

import java.sql.SQLException;
import java.util.List;

public interface ITaskDao {
    Task createTask(Task t) throws SQLException;
    boolean updateTask(Task t) throws SQLException;
    boolean deleteTask(int id, Integer ownerAdminId) throws SQLException;
    Task findById(int id) throws SQLException;
    Task findById(int id, Integer ownerAdminId) throws SQLException;
    List<Task> findByEmployee(int employeeId) throws SQLException;
    List<Task> findByCreator(int adminId) throws SQLException;
    List<Task> findAll() throws SQLException;
}
