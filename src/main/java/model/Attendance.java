package model;

import java.time.LocalDateTime;

public class Attendance {
    private int id;
    private int employeeId;
    private String status; // PRESENT / ABSENT / LATE
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;

    public Attendance(){}

    public Attendance(int id, int employeeId, String status, LocalDateTime checkIn, LocalDateTime checkOut) {
        this.id = id;
        this.employeeId = employeeId;
        this.status = status;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getEmployeeId() { return employeeId; }
    public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCheckIn() { return checkIn; }
    public void setCheckIn(LocalDateTime checkIn) { this.checkIn = checkIn; }
    public LocalDateTime getCheckOut() { return checkOut; }
    public void setCheckOut(LocalDateTime checkOut) { this.checkOut = checkOut; }
}
