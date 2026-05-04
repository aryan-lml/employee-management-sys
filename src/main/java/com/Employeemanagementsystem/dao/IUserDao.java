package com.Employeemanagementsystem.dao;

import com.Employeemanagementsystem.model.User;

import java.sql.SQLException;

public interface IUserDao {
    boolean createUserWithHash(String username, String passwordHash, String role) throws SQLException;
    void createUser(String username, String plainPassword, String role) throws SQLException;
    boolean validateCredentials(String username, String plainPassword) throws SQLException;
    User findByUsername(String username) throws SQLException;
    User findById(int id) throws SQLException;
}
