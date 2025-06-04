package com.gestion_obras.models.dtos.materialrequest;

import com.gestion_obras.models.enums.MaterialQuality;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class MaterialRequestDto {
    private Long materialId;
    private Long projectId;
    private String userId;
    private int requestedQuantity;
    private String comments;
    private MaterialQuality materialQuality;
    private LocalDate deliveryDate;
}
