package com.example.taskservice.mapper;
import com.example.taskservice.dto.TaskDTO;
import com.example.taskservice.entity.Task;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface TaskMapper {

    TaskMapper INSTANCE = Mappers.getMapper(TaskMapper.class);

    TaskDTO  mapTaskToTaskDTO(Task task);
    List<TaskDTO> mapTasksToTaskDTOs(List<Task> list);
}
