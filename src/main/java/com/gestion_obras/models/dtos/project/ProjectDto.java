package com.gestion_obras.models.dtos.project;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class ProjectDto {
    private String name;
    private String description;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Integer locationRange;
    private LocalDate startDate;
    private LocalDate endDate;
    private String userId;
}