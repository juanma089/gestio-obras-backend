package com.gestion_obras.controllers;

import com.gestion_obras.models.dtos.project.ProjectDto;
import com.gestion_obras.models.dtos.user.UserInfoDto;
import com.gestion_obras.models.entities.Project;
import com.gestion_obras.models.enums.RoleType;
import com.gestion_obras.models.enums.StatusProject;
import com.gestion_obras.services.sevicesmanager.ProjectServiceManager;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projects")
@Tag(name = "Proyectos", description = "Endpoint para la gestión de proyectos")
public class ProjectController {

    @Autowired
    protected ProjectServiceManager projectServiceManager;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @GetMapping
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    @Transactional(readOnly = true)
    public List<Project> findAll() {
        return this.projectServiceManager.findAll();
    }

    @GetMapping("/my-projects")
    @PreAuthorize("hasAuthority('SUPERVISOR')")
    @Transactional(readOnly = true)
    public ResponseEntity<List<Project>> getProjectsByAuthenticatedUser() {
        try {
            // Obtener el usuario autenticado desde el contexto
            UserInfoDto userInfo = (UserInfoDto) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            // Buscar proyectos del usuario
            List<Project> projects = projectServiceManager.findByUserId(userInfo.getNumberID());

            return ResponseEntity.ok(projects);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    @Transactional(readOnly = true)
    public ResponseEntity<Project> findById(@PathVariable Long id) {
        return this.projectServiceManager.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public Project create(@RequestBody ProjectDto project) {
        Project projectNew = this.mapToProject(project);
        projectNew.setStatus(StatusProject.EN_PROGRESO);

        Project saveProject = this.projectServiceManager.save(projectNew);

        messagingTemplate.convertAndSend("/topic/projects", "refresh");

        return saveProject;
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SUPERVISOR', 'ADMINISTRADOR')")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody ProjectDto updatedProject) {
        return this.projectServiceManager.findById(id)
                .map(existingProject -> {
                    UserInfoDto userInfo = (UserInfoDto) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                    if (userInfo.getRole().equals(RoleType.SUPERVISOR)) {
                        updatedProject.setUserId(existingProject.getUserId());
                    }
                    if (existingProject.getStatus() == StatusProject.FINALIZADO) {
                        return ResponseEntity.badRequest().build();
                    }
                    Project project = mapToProject(updatedProject);
                    project.setId(existingProject.getId());
                    project.setStatus(existingProject.getStatus());
                    Project savedProject = this.projectServiceManager.save(project);
                    messagingTemplate.convertAndSend("/topic/projects", "refresh");
                    return ResponseEntity.ok(savedProject);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyAuthority('SUPERVISOR', 'ADMINISTRADOR')")
    public ResponseEntity<Void> updateProjectStatus(@PathVariable Long id, @RequestParam StatusProject status) {
        int rowsAffected = this.projectServiceManager.updateProjectStatus(id, status);
        if(rowsAffected > 0){
            messagingTemplate.convertAndSend("/topic/projects", "refresh");
            return ResponseEntity.noContent().build();
        }else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        return this.projectServiceManager.findById(id)
                .map(existingProject -> {
                    if (existingProject.getStatus() == StatusProject.FINALIZADO) {
                        return ResponseEntity.badRequest().build();
                    }
                    boolean deleted = this.projectServiceManager.delete(id);

                    if(deleted){
                        messagingTemplate.convertAndSend("/topic/projects", "refresh");
                        return ResponseEntity.noContent().build();
                    }else {
                        return ResponseEntity.notFound().build();
                    }
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private Project mapToProject(ProjectDto projectDto) {
        Project project = new Project();
        project.setName(projectDto.getName());
        if(projectDto.getName() != null)
            project.setName(projectDto.getName());

        if(projectDto.getDescription() != null)
            project.setDescription(projectDto.getDescription());

        if(projectDto.getLatitude() != null)
            project.setLatitude(projectDto.getLatitude());

        if(projectDto.getLocationRange() != null)
            project.setLocationRange(projectDto.getLocationRange());

        if(projectDto.getLongitude() != null) {
            project.setLongitude(projectDto.getLongitude());
        }
        if(projectDto.getStartDate() != null)
            project.setStartDate(projectDto.getStartDate());

        if(projectDto.getEndDate() != null)
            project.setEndDate(projectDto.getEndDate());

        if(projectDto.getUserId() != null)
            project.setUserId(projectDto.getUserId());

        return project;
    }

}