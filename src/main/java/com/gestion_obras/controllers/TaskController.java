package com.gestion_obras.controllers;

import com.gestion_obras.models.dtos.task.TaskDto;
import com.gestion_obras.models.entities.Task;
import com.gestion_obras.models.entities.WorkZone;
import com.gestion_obras.models.enums.StatusTask;
import com.gestion_obras.services.sevicesmanager.TaskServiceManager;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/task")
@Tag(name = "Tareas", description = "Endpoint para la gestión de tareas")
public class TaskController {

    @Autowired
    private TaskServiceManager taskServiceManager;

    @GetMapping
    @Transactional(readOnly = true)
    public List<Task> findAll() {
        return this.taskServiceManager.findAll();
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<Task> getById(@PathVariable Long id){
        return this.taskServiceManager.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public Task create(@RequestBody TaskDto task) {
        Task taskNew = this.mapToTask(task);
        taskNew.setStatus(StatusTask.PENDIENTE);
        return this.taskServiceManager.save(taskNew);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> update(@PathVariable Long id, @Valid @RequestBody TaskDto updatedTask) {
        return this.taskServiceManager.findById(id)
                .map(existingTask -> {
                    Task task = mapToTask(updatedTask);
                    task.setId(id);
                    Task savedTask = this.taskServiceManager.save(task);
                    return ResponseEntity.ok(savedTask);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boolean deleted = taskServiceManager.delete(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    private Task mapToTask(TaskDto taskDto) {
        Task task = new Task();
        if (taskDto.getZoneId() != null) {
            WorkZone zone = new WorkZone();
            zone.setId(taskDto.getZoneId());
            task.setZone(zone);
        }
        if (taskDto.getName() != null) {
            task.setName(taskDto.getName());
        }
        if (taskDto.getDescription() != null) {
            task.setDescription(taskDto.getDescription());
        }
        if (taskDto.getUserId() != null) {
            task.setUserId(taskDto.getUserId());
        }
        if (taskDto.getEvidence() != null) {
            task.setEvidence(taskDto.getEvidence());
        }
        return task;
    }
}
