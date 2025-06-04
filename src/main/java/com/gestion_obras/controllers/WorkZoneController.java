package com.gestion_obras.controllers;

import com.gestion_obras.models.dtos.user.UserInfoDto;
import com.gestion_obras.models.dtos.workzone.WorkZoneDto;
import com.gestion_obras.models.entities.Project;
import com.gestion_obras.models.entities.WorkZone;
import com.gestion_obras.models.enums.StatusProject;
import com.gestion_obras.models.enums.StatusWorkZone;
import com.gestion_obras.services.sevicesmanager.ProjectServiceManager;
import com.gestion_obras.services.sevicesmanager.WorkZoneServiceManager;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/zones")
@Tag(name = "Zonas", description = "Endpoint para la gestión de zonas de trabajo")
public class WorkZoneController {

    @Autowired
    private WorkZoneServiceManager workZoneServiceManager;

    @Autowired
    private ProjectServiceManager projectServiceManager;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @GetMapping
    @Transactional(readOnly = true)
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public List<WorkZone> findAll() {
        return this.workZoneServiceManager.findAll();
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<WorkZone> getById(@PathVariable Long id){
        return this.workZoneServiceManager.findById(id).map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/my-zones")
    @PreAuthorize("hasAuthority('SUPERVISOR')")
    public ResponseEntity<?> findZoneByUserId(){
        try {
            UserInfoDto userInfo = (UserInfoDto) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            List<WorkZone> workZones = this.workZoneServiceManager.findZoneByUserId(userInfo.getNumberID());

            return ResponseEntity.ok(workZones);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/zoneByProjectId")
    @PreAuthorize("hasAnyAuthority('SUPERVISOR', 'ADMINISTRADOR')")
    public List<WorkZone> findZoneByProjectId(@RequestParam Long projectId) {
        return this.workZoneServiceManager.findByProjectId(projectId);
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('SUPERVISOR', 'ADMINISTRADOR')")
    public ResponseEntity<?> create(@RequestBody WorkZoneDto workZone) {
        return this.projectServiceManager.findById(workZone.getProjectId())
                .map(existingProject -> {
                    if (existingProject.getStatus() != StatusProject.EN_PROGRESO) {
                        return ResponseEntity.badRequest().body(
                                Map.of(
                                        "code", 400,
                                        "message", "No se puede crear zona para un proyecto FINALIZADO o SUSPENDIDO",
                                        "status", "error"

                                )
                        );
                    }
                    WorkZone zoneNew = this.mapToZone(workZone);
                    zoneNew.setStatus(StatusWorkZone.EN_PROGRESO);
                    WorkZone savedWorkZone = this.workZoneServiceManager.save(zoneNew);

                    messagingTemplate.convertAndSend("/topic/zones", "refresh");

                    return ResponseEntity.ok(savedWorkZone);
                }).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        Map.of(
                                "code", 404,
                                "message", "No se encontró proyecto seleccionado",
                                "status", "error"
                        )
                ));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SUPERVISOR', 'ADMINISTRADOR')")
    public ResponseEntity<WorkZone> update(@PathVariable Long id, @Valid @RequestBody WorkZoneDto updatedWorkZone) {
        return this.workZoneServiceManager.findById(id)
                .map(existingWorkZone -> {
                    WorkZone zone = mapToZone(updatedWorkZone);
                    zone.setId(id);
                    zone.setStatus(existingWorkZone.getStatus());
                    WorkZone savedWorkZone = this.workZoneServiceManager.save(zone);

                    messagingTemplate.convertAndSend("/topic/zones", "refresh");

                    return ResponseEntity.ok(savedWorkZone);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyAuthority('SUPERVISOR', 'ADMINISTRADOR')")
    public ResponseEntity<Void> updateWorkZoneStatus(@PathVariable Long id, @RequestParam StatusWorkZone status){
        int rowsAffected = this.workZoneServiceManager.updateWorkZoneStatus(id, status);
        if(rowsAffected > 0){
            messagingTemplate.convertAndSend("/topic/zones", "refresh");
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SUPERVISOR', 'ADMINISTRADOR')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boolean deleted = workZoneServiceManager.delete(id);
        if(deleted) {
            messagingTemplate.convertAndSend("/topic/zones", "refresh");
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    private WorkZone mapToZone(WorkZoneDto workZoneDto) {
        WorkZone workZone = new WorkZone();
        if (workZoneDto.getProjectId() != null) {
            Project project = new Project();
            project.setId(workZoneDto.getProjectId());
            workZone.setProject(project);
        }
        if (workZoneDto.getName() != null) {
            workZone.setName(workZoneDto.getName());
        }
        if (workZoneDto.getDescription() != null) {
            workZone.setDescription(workZoneDto.getDescription());
        }
        return workZone;
    }

}
