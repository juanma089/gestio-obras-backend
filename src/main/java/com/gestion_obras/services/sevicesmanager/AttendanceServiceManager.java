package com.gestion_obras.services.sevicesmanager;

import com.gestion_obras.models.entities.Attendance;
import com.gestion_obras.repositories.AttendanceRepository;
import com.gestion_obras.services.GenericServiceManager;
import org.springframework.stereotype.Service;

@Service
public class AttendanceServiceManager extends GenericServiceManager<Attendance, AttendanceRepository> {
}