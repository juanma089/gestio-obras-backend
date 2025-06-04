package com.gestion_obras.services.sevicesmanager;

import com.gestion_obras.models.entities.Task;
import com.gestion_obras.repositories.TaskRepository;
import com.gestion_obras.services.GenericServiceManager;
import org.springframework.stereotype.Service;

@Service
public class TaskServiceManager extends GenericServiceManager<Task, TaskRepository> {
}
