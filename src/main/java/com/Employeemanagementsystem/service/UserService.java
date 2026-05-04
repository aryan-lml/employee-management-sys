package com.Employeemanagementsystem.service;

import com.Employeemanagementsystem.dao.UserDao;
import com.Employeemanagementsystem.model.User;

import java.sql.SQLException;

public class UserService implements IUserService {

    private final UserDao userDao = new UserDao();

    @Override
    public User register(String username, String plainPassword, String role) throws SQLException {
        // delegate hashing and creation to DAO
        userDao.createUser(username, plainPassword, role == null ? "EMPLOYEE" : role);
        return userDao.findByUsername(username);
    }

    @Override
    public User authenticate(String username, String plainPassword) throws SQLException {
        boolean ok = userDao.validateCredentials(username, plainPassword);
        if (!ok) return null;
        return userDao.findByUsername(username);
    }

    @Override
    public User findByUsername(String username) throws SQLException {
        return userDao.findByUsername(username);
    }
}
