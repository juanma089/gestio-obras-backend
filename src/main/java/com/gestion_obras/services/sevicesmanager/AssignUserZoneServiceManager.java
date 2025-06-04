package com.gestion_obras.services.sevicesmanager;

import com.gestion_obras.models.entities.AssignUserZone;
import com.gestion_obras.models.entities.WorkZone;
import com.gestion_obras.repositories.AssignUserZoneRepository;
import com.gestion_obras.repositories.WorkZonesRepository;
import com.gestion_obras.services.GenericServiceManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AssignUserZoneServiceManager extends GenericServiceManager<AssignUserZone, AssignUserZoneRepository> {

    @Autowired
    private WorkZonesRepository workZoneRepository;

    public Optional<WorkZone> findByUserId(String userId) {
        return repository.findByUserId(userId);
    }

    public Optional<AssignUserZone> findAssignUserZoneByUserId(String userId) {
        return repository.findAssignUserZoneByUserId(userId);
    }

    public void assignUsersToZone(Long zoneId, List<String> userIds) {
        WorkZone workZone = workZoneRepository.findById(zoneId)
                .orElseThrow(() -> new IllegalArgumentException("Zona no encontrada"));

        for (String userId : userIds) {
            Optional<AssignUserZone> existing = repository.findAssignUserZoneByUserId(userId);

            if (existing.isPresent()) {
                AssignUserZone assign = existing.get();
                if (!assign.getWorkZone().getId().equals(zoneId)) {
                    assign.setWorkZone(workZone);
                    repository.save(assign);
                }
            } else {
                AssignUserZone newAssign = new AssignUserZone();
                newAssign.setUserId(userId);
                newAssign.setWorkZone(workZone);
                repository.save(newAssign);
            }
        }
    }

}