package com.Employeemanagementsystem.service;

import com.Employeemanagementsystem.model.User;

import java.sql.SQLException;

public interface IUserService {
    User register(String username, String plainPassword, String role) throws SQLException;
    User authenticate(String username, String plainPassword) throws SQLException;
    User findByUsername(String username) throws SQLException;
}
