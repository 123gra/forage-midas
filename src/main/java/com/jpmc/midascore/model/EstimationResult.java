package com.jpmc.midascore.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EstimationResult {
    
    private String projectName;
    private ProjectDomain domain;
    private ProjectType projectType;
    
    // COCOMO Results
    private double effortPersonMonths;
    private double developmentTimeMonths;
    private double averageStaffSize;
    private double productivityLOCPerPersonMonth;
    
    // Function Point Analysis Results
    private int totalFunctionPoints;
    private double adjustedFunctionPoints;
    private double fpaEffortPersonMonths;
    
    // Cost Estimation
    private double estimatedCostUSD;
    
    // Manpower Distribution
    private ManpowerDistribution manpowerDistribution;
    
    // Technology Recommendations
    private TechnologyRecommendation technologyRecommendation;
    
    // Risk Assessment
    private String riskLevel;
    private String riskFactors;
    
    // Additional Insights
    private String recommendations;
}
