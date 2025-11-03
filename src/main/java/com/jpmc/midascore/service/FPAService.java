package com.jpmc.midascore.service;

import com.jpmc.midascore.model.ProjectRequirements;
import org.springframework.stereotype.Service;

@Service
public class FPAService {
    
    // Weights for function point components (Average complexity)
    private static final int EXTERNAL_INPUT_WEIGHT = 4;
    private static final int EXTERNAL_OUTPUT_WEIGHT = 5;
    private static final int EXTERNAL_INQUIRY_WEIGHT = 4;
    private static final int INTERNAL_FILE_WEIGHT = 10;
    private static final int EXTERNAL_INTERFACE_WEIGHT = 7;
    
    // Average productivity: 10 FP per person-month
    private static final double FP_PER_PERSON_MONTH = 10.0;
    
    public int calculateUnadjustedFunctionPoints(ProjectRequirements requirements) {
        int ufp = 0;
        
        ufp += requirements.getExternalInputs() * EXTERNAL_INPUT_WEIGHT;
        ufp += requirements.getExternalOutputs() * EXTERNAL_OUTPUT_WEIGHT;
        ufp += requirements.getExternalInquiries() * EXTERNAL_INQUIRY_WEIGHT;
        ufp += requirements.getInternalFiles() * INTERNAL_FILE_WEIGHT;
        ufp += requirements.getExternalInterfaces() * EXTERNAL_INTERFACE_WEIGHT;
        
        return ufp;
    }
    
    public double calculateValueAdjustmentFactor(ProjectRequirements requirements) {
        // Calculate Technical Complexity Factor (TCF)
        int totalInfluence = 0;
        
        // 14 General System Characteristics (simplified)
        totalInfluence += requirements.getComplexityLevel(); // Data communications
        totalInfluence += (requirements.isRequiresHighPerformance() ? 5 : 2); // Performance
        totalInfluence += (requirements.isRequiresScalability() ? 5 : 2); // Heavily used configuration
        totalInfluence += 3; // Transaction rate (average)
        totalInfluence += 3; // Online data entry (average)
        totalInfluence += 3; // End-user efficiency (average)
        totalInfluence += 3; // Online update (average)
        totalInfluence += requirements.getComplexityLevel(); // Complex processing
        totalInfluence += 3; // Reusability (average)
        totalInfluence += 3; // Installation ease (average)
        totalInfluence += 3; // Operational ease (average)
        totalInfluence += 3; // Multiple sites (average)
        totalInfluence += 3; // Facilitate change (average)
        totalInfluence += (requirements.isRequiresHighSecurity() ? 5 : 2); // Security
        
        // VAF = 0.65 + (0.01 * TotalInfluence)
        return 0.65 + (0.01 * totalInfluence);
    }
    
    public double calculateAdjustedFunctionPoints(ProjectRequirements requirements) {
        int ufp = calculateUnadjustedFunctionPoints(requirements);
        double vaf = calculateValueAdjustmentFactor(requirements);
        
        // AFP = UFP * VAF
        return ufp * vaf;
    }
    
    public double calculateEffortFromFunctionPoints(double adjustedFunctionPoints) {
        // Effort (person-months) = AFP / Productivity
        return adjustedFunctionPoints / FP_PER_PERSON_MONTH;
    }
}
