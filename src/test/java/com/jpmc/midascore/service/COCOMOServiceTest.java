package com.jpmc.midascore.service;

import com.jpmc.midascore.model.ProjectRequirements;
import com.jpmc.midascore.model.ProjectType;
import com.jpmc.midascore.model.ProjectDomain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class COCOMOServiceTest {
    
    private COCOMOService cocomoService;
    
    @BeforeEach
    void setUp() {
        cocomoService = new COCOMOService();
    }
    
    @Test
    void testCalculateEffortForOrganicProject() {
        ProjectRequirements requirements = ProjectRequirements.builder()
                .projectName("Test Project")
                .domain(ProjectDomain.WEB_APPLICATION)
                .projectType(ProjectType.ORGANIC)
                .estimatedKLOC(10.0)
                .expectedUsers(1000)
                .durationMonths(6)
                .complexityLevel(3)
                .requiresHighSecurity(false)
                .requiresHighPerformance(false)
                .requiresScalability(false)
                .requiresRealTime(false)
                .build();
        
        double effort = cocomoService.calculateEffort(requirements);
        
        assertTrue(effort > 0, "Effort should be positive");
        assertTrue(effort < 100, "Effort should be reasonable for 10 KLOC organic project");
    }
    
    @Test
    void testCalculateEffortForEmbeddedProject() {
        ProjectRequirements requirements = ProjectRequirements.builder()
                .projectName("Embedded System")
                .domain(ProjectDomain.EMBEDDED_SYSTEMS)
                .projectType(ProjectType.EMBEDDED)
                .estimatedKLOC(10.0)
                .expectedUsers(1000)
                .durationMonths(12)
                .complexityLevel(5)
                .requiresHighSecurity(true)
                .requiresHighPerformance(true)
                .requiresScalability(false)
                .requiresRealTime(true)
                .build();
        
        double effort = cocomoService.calculateEffort(requirements);
        
        assertTrue(effort > 0, "Effort should be positive");
        // Embedded projects should require more effort than organic for same KLOC
    }
    
    @Test
    void testCalculateDevelopmentTime() {
        double effort = 50.0; // 50 person-months
        
        double devTime = cocomoService.calculateDevelopmentTime(effort, ProjectType.ORGANIC);
        
        assertTrue(devTime > 0, "Development time should be positive");
        assertTrue(devTime < effort, "Development time should be less than total effort");
    }
    
    @Test
    void testCalculateAverageStaffSize() {
        double effort = 50.0;
        double developmentTime = 10.0;
        
        double staffSize = cocomoService.calculateAverageStaffSize(effort, developmentTime);
        
        assertEquals(5.0, staffSize, 0.01, "Staff size should be effort/time");
    }
    
    @Test
    void testCalculateProductivity() {
        ProjectRequirements requirements = ProjectRequirements.builder()
                .projectName("Test Project")
                .domain(ProjectDomain.WEB_APPLICATION)
                .projectType(ProjectType.ORGANIC)
                .estimatedKLOC(10.0)
                .expectedUsers(1000)
                .durationMonths(6)
                .complexityLevel(3)
                .build();
        
        double effort = 30.0;
        double productivity = cocomoService.calculateProductivity(requirements, effort);
        
        assertTrue(productivity > 0, "Productivity should be positive");
        assertEquals(10000.0 / 30.0, productivity, 0.01, "Productivity should be LOC/effort");
    }
    
    @Test
    void testComplexityMultiplierWithHighRequirements() {
        ProjectRequirements highComplexity = ProjectRequirements.builder()
                .projectName("Complex Project")
                .domain(ProjectDomain.ENTERPRISE_SOFTWARE)
                .projectType(ProjectType.EMBEDDED)
                .estimatedKLOC(50.0)
                .expectedUsers(10000)
                .durationMonths(12)
                .complexityLevel(5)
                .requiresHighSecurity(true)
                .requiresHighPerformance(true)
                .requiresScalability(true)
                .requiresRealTime(true)
                .build();
        
        ProjectRequirements lowComplexity = ProjectRequirements.builder()
                .projectName("Simple Project")
                .domain(ProjectDomain.WEB_APPLICATION)
                .projectType(ProjectType.ORGANIC)
                .estimatedKLOC(50.0)
                .expectedUsers(100)
                .durationMonths(6)
                .complexityLevel(1)
                .requiresHighSecurity(false)
                .requiresHighPerformance(false)
                .requiresScalability(false)
                .requiresRealTime(false)
                .build();
        
        double highEffort = cocomoService.calculateEffort(highComplexity);
        double lowEffort = cocomoService.calculateEffort(lowComplexity);
        
        assertTrue(highEffort > lowEffort, 
                "High complexity project should require more effort than low complexity");
    }
}
