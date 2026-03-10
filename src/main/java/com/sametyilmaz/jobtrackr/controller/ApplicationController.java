package com.sametyilmaz.jobtrackr.controller;

import com.sametyilmaz.jobtrackr.dto.*;
import com.sametyilmaz.jobtrackr.entity.ApplicationStatus;
import com.sametyilmaz.jobtrackr.service.ApplicationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationController {

        private final ApplicationService applicationService;

        @GetMapping
        public ResponseEntity<List<ApplicationResponse>> getAll(
                        @AuthenticationPrincipal UserDetails userDetails,
                        @RequestParam(required = false) ApplicationStatus status,
                        @RequestParam(required = false) String company) {
                return ResponseEntity.ok(
                                applicationService.getAll(
                                                userDetails.getUsername(), status, company));
        }

        @PostMapping
        public ResponseEntity<ApplicationResponse> create(
                        @AuthenticationPrincipal UserDetails userDetails,
                        @Valid @RequestBody ApplicationRequest request) {
                return ResponseEntity.ok(
                                applicationService.create(
                                                userDetails.getUsername(), request));
        }

        @PutMapping("/{id}")
        public ResponseEntity<ApplicationResponse> update(
                        @AuthenticationPrincipal UserDetails userDetails,
                        @PathVariable Long id,
                        @RequestBody ApplicationRequest request) {
                return ResponseEntity.ok(
                                applicationService.update(
                                                userDetails.getUsername(), id, request));
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<Void> delete(
                        @AuthenticationPrincipal UserDetails userDetails,
                        @PathVariable Long id) {
                applicationService.delete(userDetails.getUsername(), id);
                return ResponseEntity.noContent().build();
        }

        @GetMapping("/stats")
        public ResponseEntity<StatsResponse> getStats(
                        @AuthenticationPrincipal UserDetails userDetails) {
                return ResponseEntity.ok(
                                applicationService.getStats(userDetails.getUsername()));
        }
}