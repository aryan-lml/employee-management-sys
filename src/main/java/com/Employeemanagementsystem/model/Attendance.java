package com.Employeemanagementsystem.model;

import java.sql.Timestamp;
import java.util.Objects;

public class Attendance {
    private Integer id;
    private Integer employeeId;
    private Timestamp checkIn;
    private Timestamp checkOut;
    private AttendanceStatus status;
    private Double workedHours;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getEmployeeId() { return employeeId; }
    public void setEmployeeId(Integer employeeId) { this.employeeId = employeeId; }

    public Timestamp getCheckIn() { return checkIn; }
    public void setCheckIn(Timestamp checkIn) { this.checkIn = checkIn; }

    public Timestamp getCheckOut() { return checkOut; }
    public void setCheckOut(Timestamp checkOut) { this.checkOut = checkOut; }

    public AttendanceStatus getStatus() { return status; }
    public void setStatus(AttendanceStatus status) { this.status = status; }

    public Double getWorkedHours() { return workedHours; }
    public void setWorkedHours(Double workedHours) { this.workedHours = workedHours; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Attendance)) return false;
        Attendance a = (Attendance) o;
        return Objects.equals(id, a.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Attendance{id=" + id + ", employeeId=" + employeeId + ", checkIn=" + checkIn + "}";
    }
}
