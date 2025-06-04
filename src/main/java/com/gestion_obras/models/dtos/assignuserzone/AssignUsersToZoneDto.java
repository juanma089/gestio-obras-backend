package com.gestion_obras.models.dtos.assignuserzone;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AssignUsersToZoneDto {
    private Long zoneId;
    private List<String> userIds;
}
