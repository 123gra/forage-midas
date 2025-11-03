package com.jpmc.midascore.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectRequirements {
    
    @NotNull(message = "Project name is required")
    private String projectName;
    
    @NotNull(message = "Project domain is required")
    private ProjectDomain domain;
    
    @NotNull(message = "Project type is required")
    private ProjectType projectType;
    
    @Min(value = 1, message = "Estimated KLOC must be at least 1")
    private double estimatedKLOC; // Thousands of Lines of Code
    
    @Min(value = 1, message = "Expected users must be at least 1")
    private int expectedUsers;
    
    @Min(value = 1, message = "Project duration must be at least 1 month")
    private int durationMonths;
    
    // Function Point Analysis inputs
    @Min(value = 0, message = "External inputs cannot be negative")
    private int externalInputs;
    
    @Min(value = 0, message = "External outputs cannot be negative")
    private int externalOutputs;
    
    @Min(value = 0, message = "External inquiries cannot be negative")
    private int externalInquiries;
    
    @Min(value = 0, message = "Internal files cannot be negative")
    private int internalFiles;
    
    @Min(value = 0, message = "External interfaces cannot be negative")
    private int externalInterfaces;
    
    // Complexity factors (1-5 scale)
    @Min(value = 1, message = "Complexity must be between 1 and 5")
    private int complexityLevel; // 1=Very Low, 2=Low, 3=Average, 4=High, 5=Very High
    
    private boolean requiresHighSecurity;
    private boolean requiresHighPerformance;
    private boolean requiresScalability;
    private boolean requiresRealTime;
}
