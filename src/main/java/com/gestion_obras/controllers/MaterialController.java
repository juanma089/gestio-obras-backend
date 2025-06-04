package com.gestion_obras.controllers;

import com.gestion_obras.models.dtos.material.MaterialDto;
import com.gestion_obras.models.entities.Material;
import com.gestion_obras.services.sevicesmanager.MaterialServiceManager;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/materials")
@Tag(name = "Materiales", description = "Endpoint para la gestión de materiales")
public class MaterialController {

    @Autowired
    private MaterialServiceManager materialServiceManager;

    @GetMapping
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyAuthority('SUPERVISOR', 'ADMINISTRADOR', 'OPERADOR')")
    public List<Material> findAll() {
        return this.materialServiceManager.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SUPERVISOR', 'ADMINISTRADOR', 'OPERADOR')")
    @Transactional(readOnly = true)
    public ResponseEntity<Material> getById(@PathVariable Long id) {
        return this.materialServiceManager.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('SUPERVISOR', 'ADMINISTRADOR', 'OPERADOR')")
    public Material create(@RequestBody MaterialDto material) {
        Material materialNew = this.mapToMaterial(material);
        return this.materialServiceManager.save(materialNew);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SUPERVISOR', 'ADMINISTRADOR')")
    public ResponseEntity<Material> update(@PathVariable Long id, @Valid @RequestBody MaterialDto updatedMaterial){
        return this.materialServiceManager.findById(id)
                .map(existingMaterial -> {
                    Material material = mapToMaterial(updatedMaterial);
                    material.setId(id);
                    Material savedMaterial = this.materialServiceManager.save(material);
                    return ResponseEntity.ok(savedMaterial);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SUPERVISOR', 'ADMINISTRADOR')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boolean deleted = materialServiceManager.delete(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    private Material mapToMaterial(MaterialDto materialDto) {
        Material material = new Material();
        if (materialDto.getName() != null) {
            material.setName(materialDto.getName());
        }
        if (materialDto.getUnit() != null) {
            material.setUnit(materialDto.getUnit());
        }
        return material;
    }

}
