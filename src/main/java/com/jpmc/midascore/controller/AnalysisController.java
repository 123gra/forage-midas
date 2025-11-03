package com.jpmc.midascore.controller;

import com.jpmc.midascore.model.EstimationResult;
import com.jpmc.midascore.model.ProjectRequirements;
import com.jpmc.midascore.service.ProjectAnalysisService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analysis")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AnalysisController {
    
    private final ProjectAnalysisService projectAnalysisService;
    
    @PostMapping("/analyze")
    public ResponseEntity<EstimationResult> analyzeProject(@Valid @RequestBody ProjectRequirements requirements) {
        EstimationResult result = projectAnalysisService.analyzeProject(requirements);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Analysis service is running");
    }
}
