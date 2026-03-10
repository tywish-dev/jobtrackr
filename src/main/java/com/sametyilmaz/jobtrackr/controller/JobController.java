package com.sametyilmaz.jobtrackr.controller;

import com.sametyilmaz.jobtrackr.dto.ExtractRequest;
import com.sametyilmaz.jobtrackr.service.JobExtractorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobExtractorService jobExtractorService;

    @PostMapping("/extract")
    public ResponseEntity<Map<String, Object>> extract(
            @RequestBody ExtractRequest request) {
        return ResponseEntity.ok(
                jobExtractorService.extractFromUrl(request.getUrl()));
    }
}