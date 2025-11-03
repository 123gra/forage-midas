package com.jpmc.midascore.service;

import com.jpmc.midascore.model.ManpowerDistribution;
import com.jpmc.midascore.model.ProjectRequirements;
import com.jpmc.midascore.model.ProjectType;
import org.springframework.stereotype.Service;

@Service
public class ManpowerAnalysisService {
    
    public ManpowerDistribution analyzeManpower(ProjectRequirements requirements, double effortPersonMonths, 
                                                double developmentTimeMonths) {
        ManpowerDistribution distribution = new ManpowerDistribution();
        
        // Calculate total team size based on effort and time
        int totalTeamSize = calculateTeamSize(effortPersonMonths, developmentTimeMonths);
        distribution.setTotalTeamSize(totalTeamSize);
        
        // Distribute roles based on project type and size
        distributeRoles(distribution, requirements, totalTeamSize);
        
        // Calculate phase-based effort distribution
        distributeEffortByPhase(distribution, effortPersonMonths, requirements);
        
        // Generate recommendations
        distribution.setRecommendations(generateRecommendations(requirements, totalTeamSize));
        
        return distribution;
    }
    
    private int calculateTeamSize(double effortPersonMonths, double developmentTimeMonths) {
        if (developmentTimeMonths == 0) return 1;
        
        // Average team size with some buffer
        int baseTeamSize = (int) Math.ceil(effortPersonMonths / developmentTimeMonths);
        
        // Add buffer for coordination overhead (Brooks' Law consideration)
        if (baseTeamSize > 10) {
            baseTeamSize = (int) (baseTeamSize * 1.15);
        }
        
        return Math.max(3, baseTeamSize); // Minimum team of 3
    }
    
    private void distributeRoles(ManpowerDistribution distribution, ProjectRequirements requirements, 
                                 int totalTeamSize) {
        ProjectType type = requirements.getProjectType();
        double kloc = requirements.getEstimatedKLOC();
        
        // Project Managers (1 per 8-10 team members)
        distribution.setProjectManagers(Math.max(1, totalTeamSize / 10));
        
        // Tech Leads (1 per 5-7 developers)
        int developers = (int) (totalTeamSize * 0.6); // ~60% developers
        distribution.setTechLeads(Math.max(1, developers / 6));
        
        // Developer distribution based on project complexity
        if (type == ProjectType.EMBEDDED || requirements.getComplexityLevel() >= 4) {
            // More senior developers for complex projects
            distribution.setSeniorDevelopers((int) (developers * 0.4));
            distribution.setMidLevelDevelopers((int) (developers * 0.35));
            distribution.setJuniorDevelopers((int) (developers * 0.25));
        } else if (type == ProjectType.SEMI_DETACHED) {
            distribution.setSeniorDevelopers((int) (developers * 0.3));
            distribution.setMidLevelDevelopers((int) (developers * 0.4));
            distribution.setJuniorDevelopers((int) (developers * 0.3));
        } else {
            // Organic projects can have more junior developers
            distribution.setSeniorDevelopers((int) (developers * 0.25));
            distribution.setMidLevelDevelopers((int) (developers * 0.35));
            distribution.setJuniorDevelopers((int) (developers * 0.4));
        }
        
        // QA Engineers (1 per 3-4 developers)
        distribution.setQaEngineers(Math.max(1, developers / 4));
        
        // DevOps Engineers (based on scalability and deployment needs)
        if (requirements.isRequiresScalability() || kloc > 50) {
            distribution.setDevOpsEngineers(Math.max(1, totalTeamSize / 8));
        } else {
            distribution.setDevOpsEngineers(1);
        }
        
        // UI/UX Designers (for user-facing applications)
        if (requirements.getDomain().toString().contains("APPLICATION")) {
            distribution.setUiUxDesigners(Math.max(1, totalTeamSize / 10));
        } else {
            distribution.setUiUxDesigners(0);
        }
        
        // Business Analysts
        distribution.setBusinessAnalysts(Math.max(1, totalTeamSize / 12));
    }
    
    private void distributeEffortByPhase(ManpowerDistribution distribution, double totalEffort, 
                                        ProjectRequirements requirements) {
        // Standard distribution percentages (can be adjusted based on project type)
        double requirementsPercent = 0.10;
        double designPercent = 0.15;
        double developmentPercent = 0.50;
        double testingPercent = 0.20;
        double deploymentPercent = 0.05;
        
        // Adjust for complex projects
        if (requirements.getComplexityLevel() >= 4) {
            requirementsPercent = 0.12;
            designPercent = 0.18;
            developmentPercent = 0.45;
            testingPercent = 0.20;
            deploymentPercent = 0.05;
        }
        
        // Adjust for high security requirements
        if (requirements.isRequiresHighSecurity()) {
            testingPercent += 0.05;
            developmentPercent -= 0.05;
        }
        
        distribution.setRequirementsAnalysisEffort(totalEffort * requirementsPercent);
        distribution.setDesignEffort(totalEffort * designPercent);
        distribution.setDevelopmentEffort(totalEffort * developmentPercent);
        distribution.setTestingEffort(totalEffort * testingPercent);
        distribution.setDeploymentEffort(totalEffort * deploymentPercent);
    }
    
    private String generateRecommendations(ProjectRequirements requirements, int totalTeamSize) {
        StringBuilder recommendations = new StringBuilder();
        
        recommendations.append("Team Size: ").append(totalTeamSize).append(" members. ");
        
        if (totalTeamSize > 15) {
            recommendations.append("Consider splitting into multiple sub-teams to reduce communication overhead. ");
        }
        
        if (requirements.getProjectType() == ProjectType.EMBEDDED) {
            recommendations.append("Prioritize experienced developers for embedded systems complexity. ");
        }
        
        if (requirements.isRequiresHighSecurity()) {
            recommendations.append("Include security specialists in the team. ");
        }
        
        if (requirements.isRequiresScalability()) {
            recommendations.append("Ensure DevOps team has cloud and scalability expertise. ");
        }
        
        recommendations.append("Maintain a balanced mix of senior and junior developers for knowledge transfer.");
        
        return recommendations.toString();
    }
}
