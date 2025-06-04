package com.gestion_obras.services.sevicesmanager;

import com.gestion_obras.models.entities.MaterialRequest;
import com.gestion_obras.models.enums.StatusMaterialRequest;
import com.gestion_obras.repositories.MaterialRequestRepository;
import com.gestion_obras.services.GenericServiceManager;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MaterialRequestServiceManager extends GenericServiceManager<MaterialRequest, MaterialRequestRepository> {

    public List<MaterialRequest> findByUserId(String userId) {
        return this.repository.findByUserId(userId);
    }

    public List<MaterialRequest> findByProjectId(Long projectId) {
        return this.repository.findByProjectId(projectId);
    }

    public boolean updateStatus(Long id, StatusMaterialRequest status) {
        return this.repository.findById(id).map(request -> {
            if(request.getStatus() == StatusMaterialRequest.PENDIENTE){
                request.setStatus(status);
                this.repository.save(request);
                return true;
            }else {
                return false;
            }
        }).orElse(false);
    }

}
