package com.gestion_obras.repositories;

import com.gestion_obras.models.entities.MaterialRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaterialRequestRepository extends JpaRepository<MaterialRequest, Long> {

    List<MaterialRequest> findByUserId(String userId);

    List<MaterialRequest> findByProjectId(Long projectId);
}
