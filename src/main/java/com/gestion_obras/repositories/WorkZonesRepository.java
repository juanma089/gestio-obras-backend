package com.gestion_obras.repositories;

import com.gestion_obras.models.entities.WorkZone;
import com.gestion_obras.models.enums.StatusWorkZone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkZonesRepository extends JpaRepository<WorkZone, Long> {

    @Modifying
    @Query("UPDATE WorkZone w SET w.status = :status WHERE w.id = :id")
    int updateStatusById(@Param("id") Long id, @Param("status") StatusWorkZone status);

    @Query("SELECT w FROM WorkZone w WHERE w.project.userId = :userId")
    List<WorkZone> findZoneByUserId(@Param("userId") String userId);

    @Query("SELECT w FROM WorkZone w WHERE w.project.id = :projectId")
    List<WorkZone> findByProjectId(@Param("projectId") Long projectId);
}
