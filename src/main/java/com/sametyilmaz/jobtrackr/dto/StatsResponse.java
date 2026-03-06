package com.sametyilmaz.jobtrackr.dto;

import lombok.Builder;
import lombok.Data;
import java.util.Map;

@Data
@Builder
public class StatsResponse {
    private Long total;
    private Map<String, Long> byStatus;
    private Double responseRate;
}