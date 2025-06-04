package com.gestion_obras.repositories;

import com.gestion_obras.models.entities.AssignUserZone;
import com.gestion_obras.models.entities.WorkZone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AssignUserZoneRepository extends JpaRepository<AssignUserZone, Long> {

    @Query("SELECT a.workZone FROM AssignUserZone a WHERE a.userId = :userId")
    Optional<WorkZone> findByUserId(@Param("userId") String userId);

    @Query("SELECT a FROM AssignUserZone a WHERE a.userId = :userId")
    Optional<AssignUserZone> findAssignUserZoneByUserId(@Param("userId") String userId);



}