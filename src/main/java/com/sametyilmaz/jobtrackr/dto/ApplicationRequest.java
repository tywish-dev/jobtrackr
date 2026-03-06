package com.sametyilmaz.jobtrackr.dto;

import com.sametyilmaz.jobtrackr.entity.ApplicationStatus;
import lombok.Data;
import java.time.LocalDate;

@Data
public class ApplicationRequest {
    private String company;
    private String role;
    private ApplicationStatus status;
    private String jobUrl;
    private String notes;
    private Integer salaryMin;
    private Integer salaryMax;
    private LocalDate appliedDate;
}