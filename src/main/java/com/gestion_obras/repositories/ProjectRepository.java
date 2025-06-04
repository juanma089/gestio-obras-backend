package com.gestion_obras.repositories;

import com.gestion_obras.models.entities.Project;
import com.gestion_obras.models.enums.StatusProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    @Modifying
        @Query("UPDATE Project p SET p.status = :status WHERE p.id = :id")
        int updateStatusById(@Param("id") Long id, @Param("status") StatusProject status);

    @Transactional(readOnly = true)
    @Query("SELECT p.status FROM Project p WHERE p.id = :id")
    Optional<StatusProject> findStatusById(@Param("id") Long id);

    @Transactional(readOnly = true)
    @Query("SELECT p FROM Project p WHERE p.userId = :id ORDER BY p.createdAt DESC")
    List<Project> findByUserId(@Param("id") String userId);
}