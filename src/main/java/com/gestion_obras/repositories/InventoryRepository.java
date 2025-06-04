package com.gestion_obras.repositories;

import com.gestion_obras.models.entities.Inventory;
import com.gestion_obras.models.entities.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    @Query("SELECT i FROM Inventory i WHERE i.project.id = :projectId")
    List<Inventory> findByProjectId(@Param("projectId") Long projectId);

    @Query("UPDATE Inventory i SET i.AvailableQuantity = i.AvailableQuantity + :amount WHERE i.id = :inventoryId")
    @Transactional
    @Modifying
    int addAvailableQuantity(@Param("inventoryId") Long inventoryId, @Param("amount") int amount);

    @Query("UPDATE Inventory i SET i.AvailableQuantity = i.AvailableQuantity - :amount WHERE i.id = :inventoryId AND i.AvailableQuantity >= :amount")
    @Transactional
    @Modifying
    int subtractAvailableQuantity(@Param("inventoryId") Long inventoryId, @Param("amount") int amount);

}
