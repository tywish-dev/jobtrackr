package com.sametyilmaz.jobtrackr.dto;

import com.sametyilmaz.jobtrackr.entity.Application;
import com.sametyilmaz.jobtrackr.entity.ApplicationStatus;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ApplicationResponse {
    private Long id;
    private String company;
    private String role;
    private ApplicationStatus status;
    private String jobUrl;
    private String notes;
    private Integer salaryMin;
    private Integer salaryMax;
    private LocalDate appliedDate;
    private LocalDateTime createdAt;

    public static ApplicationResponse from(Application a) {
        ApplicationResponse res = new ApplicationResponse();
        res.setId(a.getId());
        res.setCompany(a.getCompany());
        res.setRole(a.getRole());
        res.setStatus(a.getStatus());
        res.setJobUrl(a.getJobUrl());
        res.setNotes(a.getNotes());
        res.setSalaryMin(a.getSalaryMin());
        res.setSalaryMax(a.getSalaryMax());
        res.setAppliedDate(a.getAppliedDate());
        res.setCreatedAt(a.getCreatedAt());
        return res;
    }
}