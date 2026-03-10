package com.sametyilmaz.jobtrackr.dto;

import com.sametyilmaz.jobtrackr.entity.ApplicationStatus;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Data
public class ApplicationRequest {

    @NotBlank(message = "Company name is required")
    private String company;

    @NotBlank(message = "Role is required")
    private String role;

    private ApplicationStatus status;

    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;

    private String jobUrl;
    private Integer salaryMin;
    private Integer salaryMax;
    private LocalDate appliedDate;
}