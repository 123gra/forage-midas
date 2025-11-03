package com.jpmc.midascore.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ManpowerDistribution {
    
    private int totalTeamSize;
    
    // Role-based distribution
    private int projectManagers;
    private int techLeads;
    private int seniorDevelopers;
    private int midLevelDevelopers;
    private int juniorDevelopers;
    private int qaEngineers;
    private int devOpsEngineers;
    private int uiUxDesigners;
    private int businessAnalysts;
    
    // Phase-based effort distribution (in person-months)
    private double requirementsAnalysisEffort;
    private double designEffort;
    private double developmentEffort;
    private double testingEffort;
    private double deploymentEffort;
    
    private String recommendations;
}
