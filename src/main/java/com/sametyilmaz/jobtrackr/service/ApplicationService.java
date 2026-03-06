package com.sametyilmaz.jobtrackr.service;

import com.sametyilmaz.jobtrackr.dto.*;
import com.sametyilmaz.jobtrackr.entity.*;
import com.sametyilmaz.jobtrackr.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public List<ApplicationResponse> getAll(
            String email, ApplicationStatus status, String company) {
        User user = getUser(email);

        List<Application> apps;
        if (status != null) {
            apps = applicationRepository
                    .findByUserIdAndStatus(user.getId(), status);
        } else if (company != null && !company.isEmpty()) {
            apps = applicationRepository
                    .findByUserIdAndCompanyContainingIgnoreCase(
                            user.getId(), company);
        } else {
            apps = applicationRepository.findByUserId(user.getId());
        }

        return apps.stream()
                .map(ApplicationResponse::from)
                .collect(Collectors.toList());
    }

    public ApplicationResponse create(
            String email, ApplicationRequest request) {
        User user = getUser(email);

        Application app = Application.builder()
                .user(user)
                .company(request.getCompany())
                .role(request.getRole())
                .status(request.getStatus() != null
                        ? request.getStatus()
                        : ApplicationStatus.APPLIED)
                .jobUrl(request.getJobUrl())
                .notes(request.getNotes())
                .salaryMin(request.getSalaryMin())
                .salaryMax(request.getSalaryMax())
                .appliedDate(request.getAppliedDate())
                .build();

        return ApplicationResponse.from(
                applicationRepository.save(app));
    }

    public ApplicationResponse update(
            String email, Long id, ApplicationRequest request) {
        User user = getUser(email);

        Application app = applicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        if (!app.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        if (request.getCompany() != null)
            app.setCompany(request.getCompany());
        if (request.getRole() != null)
            app.setRole(request.getRole());
        if (request.getStatus() != null)
            app.setStatus(request.getStatus());
        if (request.getJobUrl() != null)
            app.setJobUrl(request.getJobUrl());
        if (request.getNotes() != null)
            app.setNotes(request.getNotes());
        if (request.getSalaryMin() != null)
            app.setSalaryMin(request.getSalaryMin());
        if (request.getSalaryMax() != null)
            app.setSalaryMax(request.getSalaryMax());
        if (request.getAppliedDate() != null)
            app.setAppliedDate(request.getAppliedDate());

        return ApplicationResponse.from(
                applicationRepository.save(app));
    }

    public void delete(String email, Long id) {
        User user = getUser(email);

        Application app = applicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        if (!app.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        applicationRepository.delete(app);
    }

    public StatsResponse getStats(String email) {
        User user = getUser(email);
        Long userId = user.getId();

        Long total = applicationRepository.countByUserId(userId);

        Map<String, Long> byStatus = Arrays.stream(ApplicationStatus.values())
                .collect(Collectors.toMap(
                        Enum::name,
                        s -> applicationRepository
                                .countByUserIdAndStatus(userId, s)));

        long responded = byStatus.getOrDefault("INTERVIEW", 0L)
                + byStatus.getOrDefault("OFFER", 0L)
                + byStatus.getOrDefault("REJECTED", 0L);

        double responseRate = total > 0
                ? Math.round((responded * 100.0 / total) * 10.0) / 10.0
                : 0.0;

        return StatsResponse.builder()
                .total(total)
                .byStatus(byStatus)
                .responseRate(responseRate)
                .build();
    }
}