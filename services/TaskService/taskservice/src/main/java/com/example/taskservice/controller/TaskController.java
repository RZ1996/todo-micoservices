package com.example.taskservice.controller;

import com.example.taskservice.dto.TaskDTO;
import com.example.taskservice.entity.Task;
import com.example.taskservice.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@CrossOrigin(
        origins = "http://localhost:4200",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PATCH, RequestMethod.DELETE, RequestMethod.OPTIONS},
        allowedHeaders = {"Content-Type","Authorization"}
)
@RequestMapping("/tasks") // třídní prefix
public class TaskController {

    @Autowired
    TaskService taskService;

    @GetMapping("/{id}")
    public ResponseEntity<TaskDTO> getTask(@PathVariable int id) {
        return ResponseEntity.ok(taskService.findTask(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TaskDTO>> getTasks(@PathVariable int userId) {
        return ResponseEntity.ok(taskService.getAllTasks(userId));
    }

    @PostMapping
    public ResponseEntity<TaskDTO> createTask(@RequestBody Task task) {
        TaskDTO createdTask = taskService.createTask(task);
        return ResponseEntity
                .created(URI.create("/tasks/" + createdTask.getId()))
                .body(createdTask);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<TaskDTO> updateTask(@PathVariable int id, @RequestBody Task task) {
        return ResponseEntity.ok(taskService.updateTask(id, task));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable int id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
