package com.gestion_obras.services.sevicesmanager;

import com.gestion_obras.models.entities.Inventory;
import com.gestion_obras.repositories.InventoryRepository;
import com.gestion_obras.services.GenericServiceManager;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryServiceManager extends GenericServiceManager<Inventory, InventoryRepository> {

    public List<Inventory> findByProjectId(Long projectId) {
        return this.repository.findByProjectId(projectId);
    }

    public boolean addAvailableQuantity(Long inventoryId, int amount) {
        return repository.addAvailableQuantity(inventoryId, amount) > 0;
    }

    public boolean subtractAvailableQuantity(Long inventoryId, int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("La cantidad a restar debe ser positiva");
        }

        int rowsAffected = repository.subtractAvailableQuantity(inventoryId, amount);

        if (rowsAffected == 0) {
            if (!repository.existsById(inventoryId)) {
                throw new EntityNotFoundException("Inventario no encontrado");
            }
            return false;
        }
        return true;
    }

}
