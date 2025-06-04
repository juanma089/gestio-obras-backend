package com.gestion_obras.models.entities;

import com.gestion_obras.models.enums.PriorityTask;
import com.gestion_obras.models.enums.StatusTask;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "zone_id", nullable = false)
    private WorkZone zone;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @JoinColumn(name = "assigned_to", nullable = false)
    private String userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "ENUM('PENDIENTE', 'EN_PROGRESO', 'COMPLETADA')")
    private StatusTask status;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false, columnDefinition = "ENUM('ALTA', 'MEDIA', 'BAJA')")
    private PriorityTask priorityTask;

    @Column(name = "evidence", columnDefinition = "TEXT")
    private String evidence;

}