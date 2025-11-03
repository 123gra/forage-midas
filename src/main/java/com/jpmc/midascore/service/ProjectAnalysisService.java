package com.jpmc.midascore.service;

import com.jpmc.midascore.model.EstimationResult;
import com.jpmc.midascore.model.ManpowerDistribution;
import com.jpmc.midascore.model.ProjectRequirements;
import com.jpmc.midascore.model.TechnologyRecommendation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectAnalysisService {
    
    private final COCOMOService cocomoService;
    private final FPAService fpaService;
    private final TechnologyRecommendationService technologyService;
    private final ManpowerAnalysisService manpowerService;
    
    private static final double AVERAGE_COST_PER_PERSON_MONTH = 10000.0; // USD
    
    public EstimationResult analyzeProject(ProjectRequirements requirements) {
        EstimationResult result = new EstimationResult();
        
        // Set basic project info
        result.setProjectName(requirements.getProjectName());
        result.setDomain(requirements.getDomain());
        result.setProjectType(requirements.getProjectType());
        
        // COCOMO Calculations
        double cocomoEffort = cocomoService.calculateEffort(requirements);
        double developmentTime = cocomoService.calculateDevelopmentTime(cocomoEffort, requirements.getProjectType());
        double averageStaffSize = cocomoService.calculateAverageStaffSize(cocomoEffort, developmentTime);
        double productivity = cocomoService.calculateProductivity(requirements, cocomoEffort);
        
        result.setEffortPersonMonths(cocomoEffort);
        result.setDevelopmentTimeMonths(developmentTime);
        result.setAverageStaffSize(averageStaffSize);
        result.setProductivityLOCPerPersonMonth(productivity);
        
        // Function Point Analysis
        int totalFP = fpaService.calculateUnadjustedFunctionPoints(requirements);
        double adjustedFP = fpaService.calculateAdjustedFunctionPoints(requirements);
        double fpaEffort = fpaService.calculateEffortFromFunctionPoints(adjustedFP);
        
        result.setTotalFunctionPoints(totalFP);
        result.setAdjustedFunctionPoints(adjustedFP);
        result.setFpaEffortPersonMonths(fpaEffort);
        
        // Cost Estimation (using average of COCOMO and FPA)
        double averageEffort = (cocomoEffort + fpaEffort) / 2.0;
        double estimatedCost = averageEffort * AVERAGE_COST_PER_PERSON_MONTH;
        result.setEstimatedCostUSD(estimatedCost);
        
        // Manpower Distribution
        ManpowerDistribution manpower = manpowerService.analyzeManpower(requirements, averageEffort, developmentTime);
        result.setManpowerDistribution(manpower);
        
        // Technology Recommendations
        TechnologyRecommendation techRecommendation = technologyService.recommendTechnologies(requirements);
        result.setTechnologyRecommendation(techRecommendation);
        
        // Risk Assessment
        assessRisk(result, requirements);
        
        // Generate overall recommendations
        result.setRecommendations(generateOverallRecommendations(requirements, result));
        
        return result;
    }
    
    private void assessRisk(EstimationResult result, ProjectRequirements requirements) {
        int riskScore = 0;
        StringBuilder riskFactors = new StringBuilder();
        
        // Size risk
        if (requirements.getEstimatedKLOC() > 100) {
            riskScore += 2;
            riskFactors.append("Large project size (>100 KLOC). ");
        }
        
        // Complexity risk
        if (requirements.getComplexityLevel() >= 4) {
            riskScore += 2;
            riskFactors.append("High complexity level. ");
        }
        
        // Team size risk
        if (result.getManpowerDistribution().getTotalTeamSize() > 20) {
            riskScore += 1;
            riskFactors.append("Large team size may cause communication overhead. ");
        }
        
        // Technology risk
        if (requirements.getProjectType().toString().equals("EMBEDDED")) {
            riskScore += 1;
            riskFactors.append("Embedded systems have inherent complexity. ");
        }
        
        // Timeline risk
        if (result.getDevelopmentTimeMonths() > 24) {
            riskScore += 1;
            riskFactors.append("Long development timeline increases risk. ");
        }
        
        // Requirements risk
        if (requirements.isRequiresHighSecurity() && requirements.isRequiresHighPerformance()) {
            riskScore += 1;
            riskFactors.append("Multiple critical requirements increase complexity. ");
        }
        
        // Determine risk level
        String riskLevel;
        if (riskScore <= 2) {
            riskLevel = "LOW";
        } else if (riskScore <= 4) {
            riskLevel = "MEDIUM";
        } else if (riskScore <= 6) {
            riskLevel = "HIGH";
        } else {
            riskLevel = "VERY HIGH";
        }
        
        result.setRiskLevel(riskLevel);
        result.setRiskFactors(riskFactors.toString());
    }
    
    private String generateOverallRecommendations(ProjectRequirements requirements, EstimationResult result) {
        StringBuilder recommendations = new StringBuilder();
        
        recommendations.append("Project Analysis Summary: ");
        recommendations.append(String.format("Estimated effort: %.1f person-months, ", result.getEffortPersonMonths()));
        recommendations.append(String.format("Timeline: %.1f months, ", result.getDevelopmentTimeMonths()));
        recommendations.append(String.format("Team size: %d members. ", result.getManpowerDistribution().getTotalTeamSize()));
        
        if (result.getRiskLevel().equals("HIGH") || result.getRiskLevel().equals("VERY HIGH")) {
            recommendations.append("⚠️ High risk project - implement strong project management and monitoring. ");
        }
        
        recommendations.append("Use iterative development methodology (Agile/Scrum) for better adaptability. ");
        
        if (requirements.getEstimatedKLOC() > 50) {
            recommendations.append("Consider modular architecture for maintainability. ");
        }
        
        recommendations.append("Regular code reviews and automated testing are essential. ");
        
        if (requirements.isRequiresScalability()) {
            recommendations.append("Design for scalability from day one - refactoring later is costly. ");
        }
        
        return recommendations.toString();
    }
}
