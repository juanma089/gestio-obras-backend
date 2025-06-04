package com.gestion_obras.controllers;

import com.gestion_obras.models.dtos.attendance.AttendanceDto;
import com.gestion_obras.models.entities.Attendance;
import com.gestion_obras.models.entities.WorkZone;
import com.gestion_obras.services.sevicesmanager.AttendanceServiceManager;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/attendances")
@Tag(name = "Asistencias", description = "Endpoint para la gestión de asistencias")
public class AttendanceController {

    @Autowired
    private AttendanceServiceManager attendanceServiceManager;

    @GetMapping
    @Transactional(readOnly = true)
    public List<Attendance> findAll() {
        return this.attendanceServiceManager.findAll();
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<Attendance> getById(@PathVariable Long id) {
        return this.attendanceServiceManager.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public Attendance create(@RequestBody AttendanceDto attendance) {
        Attendance AttendanceNew = this.mapToAttendance(attendance);
        return this.attendanceServiceManager.save(AttendanceNew);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Attendance> update(@PathVariable Long id, @Valid @RequestBody AttendanceDto updatedAttendance) {
        return this.attendanceServiceManager.findById(id)
                .map(existingAttendance -> {
                    Attendance attendance = mapToAttendance(updatedAttendance);
                    attendance.setId(id);
                    Attendance savedAttendance = this.attendanceServiceManager.save(attendance);
                    return ResponseEntity.ok(savedAttendance);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/check-out/{id}")
    public ResponseEntity<Attendance> checkOut(@PathVariable Long id) {
        return this.attendanceServiceManager.findById(id)
                .map(existingAttendance -> {
                    existingAttendance.setCheckOut(LocalDateTime.now());
                    Attendance savedAttendance = this.attendanceServiceManager.save(existingAttendance);
                    return ResponseEntity.ok(savedAttendance);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Long id) {
        return this.attendanceServiceManager.findById(id)
                .map(existingAttendance -> {
                    this.attendanceServiceManager.delete(existingAttendance.getId());
                    return ResponseEntity.noContent().build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private Attendance mapToAttendance(AttendanceDto attendance) {
        Attendance attendanceNew = new Attendance();

        if(attendance.getUserId() != null) {
            attendanceNew.setUserId(attendance.getUserId());
        }

        if(attendance.getZoneId() != null) {
            WorkZone zone = new WorkZone();
            zone.setId(attendance.getZoneId());
            attendanceNew.setZone(zone);
        }

        return attendanceNew;

    }

}
