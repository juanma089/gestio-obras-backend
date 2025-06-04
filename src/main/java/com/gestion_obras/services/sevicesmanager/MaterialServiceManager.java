package com.gestion_obras.services.sevicesmanager;

import com.gestion_obras.models.entities.Material;
import com.gestion_obras.repositories.MaterialRepository;
import com.gestion_obras.services.GenericServiceManager;
import org.springframework.stereotype.Service;

@Service
public class MaterialServiceManager extends GenericServiceManager<Material, MaterialRepository> {
}