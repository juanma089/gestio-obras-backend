package com.gestion_obras.models.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "assign_user_zones", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "zone_id"}),
        @UniqueConstraint(columnNames = {"user_id"})
})
public class AssignUserZone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @ManyToOne
    @JoinColumn(name = "zone_id", nullable = false)
    private WorkZone workZone;
}