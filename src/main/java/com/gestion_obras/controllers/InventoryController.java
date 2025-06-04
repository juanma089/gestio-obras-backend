package com.gestion_obras.controllers;

import com.gestion_obras.models.dtos.inventory.InventoryDto;
import com.gestion_obras.models.dtos.user.UserInfoDto;
import com.gestion_obras.models.entities.Inventory;
import com.gestion_obras.models.entities.Material;
import com.gestion_obras.models.entities.Project;
import com.gestion_obras.models.enums.RoleType;
import com.gestion_obras.services.sevicesmanager.InventoryServiceManager;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inventories")
@Tag(name = "Inventarios", description = "Endpoint para la gestión de inventarios")
public class InventoryController {

    @Autowired
    private InventoryServiceManager inventoryServiceManager;

    @GetMapping
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyAuthority('SUPERVISOR', 'ADMINISTRADOR')")
    public List<Inventory> findAll() {
        UserInfoDto userInfo = (UserInfoDto) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (RoleType.ADMINISTRADOR.equals(userInfo.getRole())) {
            return this.inventoryServiceManager.findAll();
        }

        // Si es SUPERVISOR, filtra los inventarios
        return this.inventoryServiceManager.findAll().stream()
                .filter(inventory -> inventory.getProject().getUserId().equals(userInfo.getNumberID()))
                .toList();
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    @Transactional(readOnly = true)
    public ResponseEntity<Inventory> getById(@PathVariable Long id) {
        return this.inventoryServiceManager.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/inventories-by-project/{projectId}")
    @PreAuthorize("hasAnyAuthority('SUPERVISOR', 'ADMINISTRADOR')")
    @Transactional(readOnly = true)
    public ResponseEntity<List<Inventory>> findByProjectId(@PathVariable Long projectId) {
        List<Inventory> inventories = inventoryServiceManager.findByProjectId(projectId);
        if (inventories == null || inventories.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(inventories);
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('SUPERVISOR', 'ADMINISTRADOR')")
    public Inventory create(@RequestBody InventoryDto inventory) {
        Inventory inventoryNew = this.mapToInventory(inventory);
        return this.inventoryServiceManager.save(inventoryNew);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SUPERVISOR', 'ADMINISTRADOR')")
    public ResponseEntity<Inventory> update(@PathVariable Long id, @Valid @RequestBody InventoryDto updatedInventory) {
        return this.inventoryServiceManager.findById(id)
                .map(existingInventory -> {
                    Inventory inventory = mapToInventory(updatedInventory);
                    inventory.setId(id);
                    Inventory savedInventory = this.inventoryServiceManager.save(inventory);
                    return ResponseEntity.ok(savedInventory);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/add-quantity")
    @PreAuthorize("hasAnyAuthority('SUPERVISOR', 'ADMINISTRADOR')")
    public ResponseEntity<Void> addAvailableQuantity(@PathVariable Long id, @RequestParam int amount) {
        boolean updated = inventoryServiceManager.addAvailableQuantity(id, amount);
        return updated ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    @PatchMapping("/{id}/subtract-quantity")
    @PreAuthorize("hasAnyAuthority('SUPERVISOR', 'ADMINISTRADOR')")
    public ResponseEntity<Void> subtractAvailableQuantity(@PathVariable Long id, @RequestParam int amount) {
        boolean updated = inventoryServiceManager.subtractAvailableQuantity(id, amount);
        return updated ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boolean deleted = inventoryServiceManager.delete(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    private Inventory mapToInventory(InventoryDto inventoryDto) {
        Inventory inventory = new Inventory();
        if (inventoryDto.getProjectId() != null) {
            Project project = new Project();
            project.setId(inventoryDto.getProjectId());
            inventory.setProject(project);
        }
        if (inventoryDto.getMaterialId() != null) {
            Material material = new Material();
            material.setId(inventoryDto.getMaterialId());
            inventory.setMaterial(material);
        }
        return inventory;
    }

}
