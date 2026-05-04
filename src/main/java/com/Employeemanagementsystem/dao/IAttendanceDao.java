package com.Employeemanagementsystem.dao;

import com.Employeemanagementsystem.model.Attendance;

import java.sql.SQLException;
import java.util.List;

public interface IAttendanceDao {
    Attendance getTodayAttendanceForEmployee(int employeeId) throws SQLException;
    boolean checkIn(Attendance attendance) throws SQLException;
    boolean checkOut(int attendanceId, java.sql.Timestamp checkOut, double workedHours, String status) throws SQLException;
    List<Attendance> getAttendanceForEmployee(int employeeId) throws SQLException;
    List<Attendance> getAllAttendance() throws SQLException;
}
