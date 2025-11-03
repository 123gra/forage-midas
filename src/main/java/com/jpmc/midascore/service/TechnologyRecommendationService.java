package com.jpmc.midascore.service;

import com.jpmc.midascore.model.ProjectDomain;
import com.jpmc.midascore.model.ProjectRequirements;
import com.jpmc.midascore.model.TechnologyRecommendation;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class TechnologyRecommendationService {
    
    public TechnologyRecommendation recommendTechnologies(ProjectRequirements requirements) {
        TechnologyRecommendation recommendation = new TechnologyRecommendation();
        
        recommendation.setFrontendTechnologies(recommendFrontend(requirements));
        recommendation.setBackendTechnologies(recommendBackend(requirements));
        recommendation.setDatabases(recommendDatabases(requirements));
        recommendation.setCloudPlatforms(recommendCloudPlatforms(requirements));
        recommendation.setDevOpsTools(recommendDevOpsTools(requirements));
        recommendation.setTestingFrameworks(recommendTestingFrameworks(requirements));
        recommendation.setAdditionalTools(recommendAdditionalTools(requirements));
        recommendation.setReasoning(generateReasoning(requirements));
        recommendation.setConsiderations(generateConsiderations(requirements));
        
        return recommendation;
    }
    
    private List<String> recommendFrontend(ProjectRequirements requirements) {
        List<String> frontend = new ArrayList<>();
        
        switch (requirements.getDomain()) {
            case WEB_APPLICATION:
                frontend.addAll(Arrays.asList("React.js", "Vue.js", "Angular"));
                if (requirements.isRequiresHighPerformance()) {
                    frontend.add("Next.js (SSR)");
                }
                break;
            case MOBILE_APPLICATION:
                frontend.addAll(Arrays.asList("React Native", "Flutter", "Swift (iOS)", "Kotlin (Android)"));
                break;
            case ENTERPRISE_SOFTWARE:
                frontend.addAll(Arrays.asList("Angular", "React.js", "TypeScript"));
                break;
            case DESKTOP_APPLICATION:
                frontend.addAll(Arrays.asList("Electron", "JavaFX", "Qt"));
                break;
            default:
                frontend.addAll(Arrays.asList("React.js", "Vue.js"));
        }
        
        return frontend;
    }
    
    private List<String> recommendBackend(ProjectRequirements requirements) {
        List<String> backend = new ArrayList<>();
        
        if (requirements.isRequiresHighPerformance()) {
            backend.addAll(Arrays.asList("Go", "Rust", "Node.js (Express)", "Spring Boot (Java)"));
        } else if (requirements.getDomain() == ProjectDomain.ENTERPRISE_SOFTWARE) {
            backend.addAll(Arrays.asList("Spring Boot (Java)", ".NET Core", "Django (Python)"));
        } else if (requirements.getDomain() == ProjectDomain.DATA_ANALYTICS || 
                   requirements.getDomain() == ProjectDomain.MACHINE_LEARNING) {
            backend.addAll(Arrays.asList("Python (FastAPI/Flask)", "R", "Scala (Spark)"));
        } else {
            backend.addAll(Arrays.asList("Node.js (Express)", "Spring Boot", "Django", "FastAPI"));
        }
        
        return backend;
    }
    
    private List<String> recommendDatabases(ProjectRequirements requirements) {
        List<String> databases = new ArrayList<>();
        
        if (requirements.isRequiresScalability() && requirements.getExpectedUsers() > 100000) {
            databases.addAll(Arrays.asList("PostgreSQL (with replication)", "MongoDB (sharded)", 
                                          "Cassandra", "Redis (caching)"));
        } else if (requirements.getDomain() == ProjectDomain.DATA_ANALYTICS) {
            databases.addAll(Arrays.asList("PostgreSQL", "MongoDB", "ClickHouse", "Elasticsearch"));
        } else if (requirements.getDomain() == ProjectDomain.ENTERPRISE_SOFTWARE) {
            databases.addAll(Arrays.asList("PostgreSQL", "Oracle", "MySQL", "SQL Server"));
        } else {
            databases.addAll(Arrays.asList("PostgreSQL", "MySQL", "MongoDB"));
        }
        
        if (requirements.isRequiresHighPerformance()) {
            databases.add("Redis (in-memory cache)");
        }
        
        return databases;
    }
    
    private List<String> recommendCloudPlatforms(ProjectRequirements requirements) {
        List<String> cloud = new ArrayList<>();
        
        if (requirements.getDomain() == ProjectDomain.ENTERPRISE_SOFTWARE) {
            cloud.addAll(Arrays.asList("AWS", "Azure", "Google Cloud Platform"));
        } else if (requirements.getDomain() == ProjectDomain.MACHINE_LEARNING) {
            cloud.addAll(Arrays.asList("AWS (SageMaker)", "Google Cloud (Vertex AI)", "Azure ML"));
        } else {
            cloud.addAll(Arrays.asList("AWS", "Google Cloud", "Azure", "DigitalOcean"));
        }
        
        if (requirements.isRequiresScalability()) {
            cloud.add("Kubernetes (container orchestration)");
        }
        
        return cloud;
    }
    
    private List<String> recommendDevOpsTools(ProjectRequirements requirements) {
        List<String> devops = new ArrayList<>();
        
        devops.addAll(Arrays.asList("Git", "GitHub/GitLab", "Docker"));
        
        if (requirements.isRequiresScalability() || requirements.getEstimatedKLOC() > 50) {
            devops.addAll(Arrays.asList("Kubernetes", "Jenkins/GitHub Actions", "Terraform"));
        } else {
            devops.add("GitHub Actions / GitLab CI");
        }
        
        devops.addAll(Arrays.asList("Prometheus (monitoring)", "Grafana (visualization)"));
        
        return devops;
    }
    
    private List<String> recommendTestingFrameworks(ProjectRequirements requirements) {
        List<String> testing = new ArrayList<>();
        
        switch (requirements.getDomain()) {
            case WEB_APPLICATION:
                testing.addAll(Arrays.asList("Jest", "Cypress", "Selenium", "JUnit"));
                break;
            case MOBILE_APPLICATION:
                testing.addAll(Arrays.asList("Appium", "XCTest", "Espresso", "Jest"));
                break;
            case ENTERPRISE_SOFTWARE:
                testing.addAll(Arrays.asList("JUnit", "TestNG", "Mockito", "Selenium"));
                break;
            default:
                testing.addAll(Arrays.asList("JUnit", "Jest", "Pytest", "Selenium"));
        }
        
        return testing;
    }
    
    private List<String> recommendAdditionalTools(ProjectRequirements requirements) {
        List<String> tools = new ArrayList<>();
        
        tools.addAll(Arrays.asList("Jira (project management)", "Confluence (documentation)", 
                                   "Slack/Teams (communication)"));
        
        if (requirements.isRequiresHighSecurity()) {
            tools.addAll(Arrays.asList("SonarQube (code quality)", "OWASP ZAP (security testing)", 
                                      "Vault (secrets management)"));
        }
        
        if (requirements.getDomain() == ProjectDomain.MACHINE_LEARNING) {
            tools.addAll(Arrays.asList("Jupyter Notebooks", "MLflow", "TensorFlow/PyTorch"));
        }
        
        return tools;
    }
    
    private String generateReasoning(ProjectRequirements requirements) {
        StringBuilder reasoning = new StringBuilder();
        reasoning.append("Technology stack recommended based on: ");
        reasoning.append("Domain (").append(requirements.getDomain()).append("), ");
        reasoning.append("Project scale (").append(requirements.getEstimatedKLOC()).append(" KLOC), ");
        reasoning.append("Expected users (").append(requirements.getExpectedUsers()).append("), ");
        
        if (requirements.isRequiresHighPerformance()) {
            reasoning.append("High performance requirements, ");
        }
        if (requirements.isRequiresScalability()) {
            reasoning.append("Scalability requirements, ");
        }
        if (requirements.isRequiresHighSecurity()) {
            reasoning.append("High security requirements, ");
        }
        
        reasoning.append("and industry best practices.");
        
        return reasoning.toString();
    }
    
    private List<String> generateConsiderations(ProjectRequirements requirements) {
        List<String> considerations = new ArrayList<>();
        
        considerations.add("Ensure team has adequate training in selected technologies");
        considerations.add("Consider licensing costs for enterprise tools");
        considerations.add("Plan for technology stack maintenance and updates");
        
        if (requirements.isRequiresScalability()) {
            considerations.add("Design for horizontal scalability from the start");
        }
        
        if (requirements.isRequiresHighSecurity()) {
            considerations.add("Implement security best practices and regular audits");
        }
        
        if (requirements.getEstimatedKLOC() > 100) {
            considerations.add("Consider microservices architecture for better maintainability");
        }
        
        return considerations;
    }
}
