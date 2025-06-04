package com.gestion_obras.controllers;

import com.gestion_obras.models.dtos.assignuserzone.AssignUserZoneDto;
import com.gestion_obras.models.dtos.assignuserzone.AssignUsersToZoneDto;
import com.gestion_obras.models.entities.AssignUserZone;
import com.gestion_obras.models.entities.WorkZone;
import com.gestion_obras.services.sevicesmanager.AssignUserZoneServiceManager;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/assign_user_zones")
@Tag(name = "Asignación de Zonas", description = "Endpoint para asignaciones de usuarios a zonas")
public class AssignUserZoneController {

    @Autowired
    private AssignUserZoneServiceManager assignUserZoneServiceManager;

    @GetMapping
    @Transactional(readOnly = true)
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public List<AssignUserZone> findAll() {
        return this.assignUserZoneServiceManager.findAll();
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<AssignUserZone> getById(@PathVariable Long id) {
        return this.assignUserZoneServiceManager.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyAuthority('SUPERVISOR', 'ADMINISTRADOR', 'OPERADOR')")
    @Transactional(readOnly = true)
    public ResponseEntity<?> getByUserId(@PathVariable String userId) {
        Optional<WorkZone> assignUserZones = this.assignUserZoneServiceManager.findByUserId(userId);
        return assignUserZones.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(assignUserZones);
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('SUPERVISOR', 'ADMINISTRADOR')")
    public AssignUserZone create(@RequestBody AssignUserZoneDto assignUserZone) {
        AssignUserZone assignUserZoneNew = this.mapToAssignUserZone(assignUserZone);
        return this.assignUserZoneServiceManager.save(assignUserZoneNew);
    }

    @PostMapping("/assignUsersToZone")
    @PreAuthorize("hasAnyAuthority('SUPERVISOR', 'ADMINISTRADOR')")
    public ResponseEntity<String> assignUsersToZone(@RequestBody AssignUsersToZoneDto dto) {
        assignUserZoneServiceManager.assignUsersToZone(dto.getZoneId(), dto.getUserIds());
        return ResponseEntity.ok("Usuarios asignados correctamente");
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SUPERVISOR', 'ADMINISTRADOR')")
    public ResponseEntity<AssignUserZone> update(@PathVariable Long id, @Valid @RequestBody AssignUserZoneDto updatedAssignUserZone) {
        return this.assignUserZoneServiceManager.findById(id)
                .map(existingAssignUserZone -> {;
                    AssignUserZone assignUserZone = mapToAssignUserZone(updatedAssignUserZone);
                    assignUserZone.setId(id);
                    AssignUserZone savedAssignUserZone = this.assignUserZoneServiceManager.save(assignUserZone);
                    return ResponseEntity.ok(savedAssignUserZone);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());

    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<Void> deleteAssignUserZone(@PathVariable Long id) {
        boolean deleted = assignUserZoneServiceManager.delete(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    public AssignUserZone mapToAssignUserZone(AssignUserZoneDto assignUserZoneDto) {
        AssignUserZone assignUserZone = new AssignUserZone();
        if (assignUserZoneDto.getUserId() != null) {
            assignUserZone.setUserId(assignUserZoneDto.getUserId());
        }
        if (assignUserZoneDto.getZoneId() != null) {
            WorkZone zone = new WorkZone();
            zone.setId(assignUserZoneDto.getZoneId());
            assignUserZone.setWorkZone(zone);
        }
        return assignUserZone;
    }

}
