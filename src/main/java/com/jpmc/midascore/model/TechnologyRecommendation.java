package com.jpmc.midascore.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TechnologyRecommendation {
    
    private List<String> frontendTechnologies;
    private List<String> backendTechnologies;
    private List<String> databases;
    private List<String> cloudPlatforms;
    private List<String> devOpsTools;
    private List<String> testingFrameworks;
    private List<String> additionalTools;
    
    private String reasoning;
    private List<String> considerations;
}
