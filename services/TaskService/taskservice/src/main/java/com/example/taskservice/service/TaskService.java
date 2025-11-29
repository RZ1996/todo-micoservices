package com.example.taskservice.service;

import com.example.taskservice.dto.TaskDTO;
import com.example.taskservice.entity.Task;
import com.example.taskservice.mapper.TaskMapper;
import com.example.taskservice.repo.TaskRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class TaskService {

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    RestTemplate restTemplate;

    private static final String USER_SERVICE_BASE = "http://localhost:9092";

    /* ===================== READ ===================== */

    @Transactional(readOnly = true)
    public TaskDTO findTask(int id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task " + id + " not found"));
        return TaskMapper.INSTANCE.mapTaskToTaskDTO(task);
    }

    @Transactional(readOnly = true)
    public List<TaskDTO> getAllTasks(int userId) {
        List<Task> tasks = taskRepository.findByUserId(userId);
        return TaskMapper.INSTANCE.mapTasksToTaskDTOs(tasks);
    }

    /* ===================== CREATE ===================== */

    @Transactional
    public TaskDTO createTask(Task task) {
        // validace a defaulty
        if (task.getUserId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId is required");
        }
        ensureUserExists(task.getUserId());

        if (task.getTitle() == null || task.getTitle().trim().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "title is required");
        }
        task.setTitle(task.getTitle().trim());

        if (task.getDescription() != null) {
            task.setDescription(task.getDescription().trim());
        }

        if (task.getStatus() == null)   task.setStatus(Task.TaskStatus.OPEN);
        if (task.getPriority() == null) task.setPriority(Task.TaskPriority.NORMAL);

        if (task.getStatus() == Task.TaskStatus.DONE && task.getCompletedAt() == null) {
            task.setCompletedAt(new java.util.Date());
        }

        Task createdTask = taskRepository.save(task);
        return TaskMapper.INSTANCE.mapTaskToTaskDTO(createdTask);
    }

    /* ===================== UPDATE ===================== */

    @Transactional
    public TaskDTO updateTask(int id, Task task) {
        Task entity = taskRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task " + id + " not found"));

        // title
        if (task.getTitle() != null) {
            String v = task.getTitle().trim();
            if (!v.isBlank()) entity.setTitle(v);
        }

        // description
        if (task.getDescription() != null) {
            String v = task.getDescription().trim();
            if (!v.isBlank()) entity.setDescription(v);
        }

        // userId – když se mění, znovu ověř existenci uživatele
        if (task.getUserId() != null && !task.getUserId().equals(entity.getUserId())) {
            ensureUserExists(task.getUserId());
            entity.setUserId(task.getUserId());
        }

        // priority
        if (task.getPriority() != null) {
            entity.setPriority(task.getPriority());
        }

        // status + completedAt
        if (task.getStatus() != null && task.getStatus() != entity.getStatus()) {
            entity.setStatus(task.getStatus());
            if (task.getStatus() == Task.TaskStatus.DONE) {
                if (entity.getCompletedAt() == null) {
                    entity.setCompletedAt(new java.util.Date());
                }
            } else {
                if (entity.getCompletedAt() != null) {
                    entity.setCompletedAt(null);
                }
            }
        }

        Task saved = taskRepository.save(entity);
        return TaskMapper.INSTANCE.mapTaskToTaskDTO(saved);
    }

    /* ===================== DELETE ===================== */

    @Transactional
    public void deleteTask(int id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task " + id + " not found"));
        taskRepository.delete(task);
    }

    /* ===================== HELPERS ===================== */

    private void ensureUserExists(Integer userId) {
        try {
            restTemplate.getForObject(USER_SERVICE_BASE + "/users/{id}", Void.class, userId);
        } catch (HttpClientErrorException.NotFound ex) {
            // uživatel neexistuje → srozumitelná 400
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User " + userId + " not found");
        } catch (ResourceAccessException ex) {
            // user-service nedostupný
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "User Service unavailable");
        } catch (HttpClientErrorException ex) {
            // ostatní chyby z user-service přepošli s původním statusem
            throw new ResponseStatusException(ex.getStatusCode(), "User Service error: " + ex.getStatusText());
        }
    }
}
