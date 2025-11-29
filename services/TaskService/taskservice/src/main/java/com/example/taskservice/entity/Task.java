package com.example.taskservice.entity;
import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

@Entity
@Data
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO )
    private Integer id;
    private Integer userId;
    private String title;
    private String description;
    private Date createdAt;
    private Date updatedAt;
    private Date completedAt;

    public enum TaskStatus {OPEN, IN_PROGRESS, DONE, CANCELLED}
    public enum TaskPriority {LOW,NORMAL,HIGH}

    @Enumerated(EnumType.STRING)
    private TaskStatus status = TaskStatus.OPEN;
    @Enumerated(EnumType.STRING)
    private  TaskPriority priority = TaskPriority.NORMAL;

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
