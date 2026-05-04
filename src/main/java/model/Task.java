package model;

import java.time.LocalDateTime;

public class Task {
    private int id;
    private int employeeId;
    private String title;
    private String description;
    private String status; // PENDING, IN_PROGRESS, COMPLETED
    private LocalDateTime createdAt;

    public Task(){}

    public Task(int id, int employeeId, String title, String description, String status, LocalDateTime createdAt) {
        this.id = id;
        this.employeeId = employeeId;
        this.title = title;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getEmployeeId() { return employeeId; }
    public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
