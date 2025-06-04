package com.gestion_obras.models.entities;

import com.gestion_obras.models.enums.MaterialQuality;
import com.gestion_obras.models.enums.StatusMaterialRequest;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "material_requests")
public class MaterialRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "material_id", nullable = false)
    private Material material;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @JoinColumn(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "requested_quantity", nullable = false)
    private int requestedQuantity;

    @Column(name = "request_date", updatable = false)
    private LocalDateTime requestDate;

    @Column(name = "delivery_date", nullable = false)
    private LocalDate deliveryDate;

    @Column(name = "comments", nullable = false)
    private String comments;

    @Enumerated(EnumType.STRING)
    @Column(name = "material_quality", nullable = false, columnDefinition = "ENUM('ALTA', 'MEDIA', 'BAJA')")
    private MaterialQuality materialQuality;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "ENUM('PENDIENTE', 'APROBADA', 'RECHAZADA')")
    private StatusMaterialRequest status;

    @PrePersist
    protected void onCreate() {
        this.requestDate = LocalDateTime.now();
    }

}