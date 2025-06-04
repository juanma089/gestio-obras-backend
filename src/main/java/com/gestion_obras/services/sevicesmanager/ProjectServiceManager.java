package com.gestion_obras.services.sevicesmanager;

import com.gestion_obras.models.entities.Project;
import com.gestion_obras.models.enums.StatusProject;
import com.gestion_obras.repositories.ProjectRepository;
import com.gestion_obras.services.GenericServiceManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProjectServiceManager extends GenericServiceManager<Project, ProjectRepository> {

    @Transactional
    public int updateProjectStatus(Long id, StatusProject status) {
        return repository.updateStatusById(id, status);
    }

    @Transactional(readOnly = true)
    public Optional<StatusProject> findStatusById(Long id){
        return repository.findStatusById(id);
    }

    @Transactional(readOnly = true)
    public List<Project> findByUserId(String id) {
        return repository.findByUserId(id);
    }

}