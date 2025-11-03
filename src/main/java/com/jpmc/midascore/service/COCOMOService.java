package com.jpmc.midascore.service;

import com.jpmc.midascore.model.ProjectRequirements;
import com.jpmc.midascore.model.ProjectType;
import org.springframework.stereotype.Service;

@Service
public class COCOMOService {
    
    // COCOMO Constants for Basic Model
    private static final double ORGANIC_A = 2.4;
    private static final double ORGANIC_B = 1.05;
    private static final double ORGANIC_C = 2.5;
    private static final double ORGANIC_D = 0.38;
    
    private static final double SEMI_DETACHED_A = 3.0;
    private static final double SEMI_DETACHED_B = 1.12;
    private static final double SEMI_DETACHED_C = 2.5;
    private static final double SEMI_DETACHED_D = 0.35;
    
    private static final double EMBEDDED_A = 3.6;
    private static final double EMBEDDED_B = 1.20;
    private static final double EMBEDDED_C = 2.5;
    private static final double EMBEDDED_D = 0.32;
    
    public double calculateEffort(ProjectRequirements requirements) {
        double kloc = requirements.getEstimatedKLOC();
        ProjectType type = requirements.getProjectType();
        
        double a = getConstantA(type);
        double b = getConstantB(type);
        
        // Effort = a * (KLOC)^b
        double baseEffort = a * Math.pow(kloc, b);
        
        // Apply complexity multiplier
        double complexityMultiplier = getComplexityMultiplier(requirements);
        
        return baseEffort * complexityMultiplier;
    }
    
    public double calculateDevelopmentTime(double effort, ProjectType type) {
        double c = getConstantC(type);
        double d = getConstantD(type);
        
        // TDEV = c * (Effort)^d
        return c * Math.pow(effort, d);
    }
    
    public double calculateAverageStaffSize(double effort, double developmentTime) {
        if (developmentTime == 0) return 0;
        return effort / developmentTime;
    }
    
    public double calculateProductivity(ProjectRequirements requirements, double effort) {
        if (effort == 0) return 0;
        return (requirements.getEstimatedKLOC() * 1000) / effort;
    }
    
    private double getConstantA(ProjectType type) {
        return switch (type) {
            case ORGANIC -> ORGANIC_A;
            case SEMI_DETACHED -> SEMI_DETACHED_A;
            case EMBEDDED -> EMBEDDED_A;
        };
    }
    
    private double getConstantB(ProjectType type) {
        return switch (type) {
            case ORGANIC -> ORGANIC_B;
            case SEMI_DETACHED -> SEMI_DETACHED_B;
            case EMBEDDED -> EMBEDDED_B;
        };
    }
    
    private double getConstantC(ProjectType type) {
        return switch (type) {
            case ORGANIC -> ORGANIC_C;
            case SEMI_DETACHED -> SEMI_DETACHED_C;
            case EMBEDDED -> EMBEDDED_C;
        };
    }
    
    private double getConstantD(ProjectType type) {
        return switch (type) {
            case ORGANIC -> ORGANIC_D;
            case SEMI_DETACHED -> SEMI_DETACHED_D;
            case EMBEDDED -> EMBEDDED_D;
        };
    }
    
    private double getComplexityMultiplier(ProjectRequirements requirements) {
        double multiplier = 1.0;
        
        // Adjust based on complexity level
        multiplier += (requirements.getComplexityLevel() - 3) * 0.1;
        
        // Additional factors
        if (requirements.isRequiresHighSecurity()) {
            multiplier += 0.15;
        }
        if (requirements.isRequiresHighPerformance()) {
            multiplier += 0.12;
        }
        if (requirements.isRequiresScalability()) {
            multiplier += 0.10;
        }
        if (requirements.isRequiresRealTime()) {
            multiplier += 0.18;
        }
        
        return Math.max(0.7, Math.min(multiplier, 2.0)); // Clamp between 0.7 and 2.0
    }
}
