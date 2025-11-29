package com.example.taskservice.dto;
import com.example.taskservice.entity.Task;
import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

@Data
public class TaskDTO {

    private int id;
    private int userId;
    private String title;
    private String description;
    private Date createdAt;
    private Date updatedAt;
    private Date completedAt;

    public enum TaskStatus {OPEN, IN_PROGRESS, DONE, CANCELLED}
    public enum TaskPriority {LOW,NORMAL,HIGH}

    @Enumerated(EnumType.STRING)
    private Task.TaskStatus status = Task.TaskStatus.OPEN;
    @Enumerated(EnumType.STRING)
    private Task.TaskPriority priority = Task.TaskPriority.NORMAL;

    @PrePersist
    void onCreate() {
        Date now = new Date();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = new Date();
    }
}
