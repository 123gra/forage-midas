# System Flow Diagram

## 🔄 Application Flow

```
┌─────────────────────────────────────────────────────────────────┐
│                         USER INTERFACE                          │
│                      (Browser - Port 8080)                      │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                      WEB CONTROLLER                             │
│                   (WebController.java)                          │
│  • GET /          → index.html (Analysis Form)                  │
│  • GET /about     → about.html (Information)                    │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                    FRONTEND (app.js)                            │
│  • Collect form data                                            │
│  • Validate inputs                                              │
│  • POST to /api/analysis/analyze                                │
│  • Display results                                              │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                   REST API CONTROLLER                           │
│                 (AnalysisController.java)                       │
│  • POST /api/analysis/analyze                                   │
│  • Validate ProjectRequirements                                 │
│  • Call ProjectAnalysisService                                  │
│  • Return EstimationResult                                      │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│              PROJECT ANALYSIS SERVICE                           │
│           (ProjectAnalysisService.java)                         │
│  • Orchestrates all analysis services                           │
│  • Combines results                                             │
│  • Generates overall recommendations                            │
└─────────────────────────────────────────────────────────────────┘
                              │
                ┌─────────────┼─────────────┐
                │             │             │
                ▼             ▼             ▼
    ┌──────────────┐  ┌──────────────┐  ┌──────────────┐
    │   COCOMO     │  │     FPA      │  │  TECHNOLOGY  │
    │   SERVICE    │  │   SERVICE    │  │  RECOMMEND   │
    │              │  │              │  │   SERVICE    │
    └──────────────┘  └──────────────┘  └──────────────┘
                              │
                              ▼
                    ┌──────────────┐
                    │  MANPOWER    │
                    │  ANALYSIS    │
                    │   SERVICE    │
                    └──────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                    ESTIMATION RESULT                            │
│  • COCOMO calculations                                          │
│  • FPA calculations                                             │
│  • Cost estimation                                              │
│  • Manpower distribution                                        │
│  • Technology recommendations                                   │
│  • Risk assessment                                              │
│  • Overall recommendations                                      │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                    RESULTS DISPLAY                              │
│  • Project Summary                                              │
│  • COCOMO Results                                               │
│  • FPA Results                                                  │
│  • Cost Estimation                                              │
│  • Manpower Distribution                                        │
│  • Technology Stack                                             │
│  • Risk Assessment                                              │
│  • Recommendations                                              │
└─────────────────────────────────────────────────────────────────┘
```

---

## 📊 Data Flow

### Input (ProjectRequirements)
```
User Input Form
    ├── Basic Info
    │   ├── Project Name
    │   ├── Domain (Web, Mobile, Enterprise, etc.)
    │   ├── Project Type (Organic, Semi-Detached, Embedded)
    │   ├── Estimated KLOC
    │   ├── Expected Users
    │   ├── Duration (months)
    │   └── Complexity Level (1-5)
    │
    ├── Function Point Analysis
    │   ├── External Inputs
    │   ├── External Outputs
    │   ├── External Inquiries
    │   ├── Internal Files
    │   └── External Interfaces
    │
    └── Special Requirements
        ├── High Security
        ├── High Performance
        ├── Scalability
        └── Real-Time Processing
```

### Processing Pipeline
```
1. COCOMO Service
   ├── Calculate base effort (a × KLOC^b)
   ├── Apply complexity multiplier
   ├── Calculate development time (c × Effort^d)
   ├── Calculate average staff size
   └── Calculate productivity

2. FPA Service
   ├── Calculate unadjusted function points
   ├── Calculate value adjustment factor
   ├── Calculate adjusted function points
   └── Calculate effort from FP

3. Technology Recommendation Service
   ├── Analyze project domain
   ├── Consider special requirements
   ├── Recommend frontend technologies
   ├── Recommend backend technologies
   ├── Recommend databases
   ├── Recommend cloud platforms
   ├── Recommend DevOps tools
   └── Generate reasoning

4. Manpower Analysis Service
   ├── Calculate total team size
   ├── Distribute roles
   │   ├── Project Managers
   │   ├── Tech Leads
   │   ├── Developers (Senior/Mid/Junior)
   │   ├── QA Engineers
   │   ├── DevOps Engineers
   │   ├── UI/UX Designers
   │   └── Business Analysts
   ├── Distribute effort by phase
   │   ├── Requirements Analysis
   │   ├── Design
   │   ├── Development
   │   ├── Testing
   │   └── Deployment
   └── Generate recommendations

5. Risk Assessment
   ├── Evaluate size risk
   ├── Evaluate complexity risk
   ├── Evaluate team size risk
   ├── Evaluate technology risk
   ├── Evaluate timeline risk
   ├── Calculate risk score
   └── Determine risk level

6. Cost Estimation
   ├── Average COCOMO and FPA effort
   ├── Apply cost per person-month
   └── Calculate total cost
```

### Output (EstimationResult)
```
Comprehensive Analysis Report
    ├── Project Summary
    │   ├── Name
    │   ├── Domain
    │   └── Type
    │
    ├── COCOMO Results
    │   ├── Effort (person-months)
    │   ├── Development Time (months)
    │   ├── Average Staff Size
    │   └── Productivity (LOC/PM)
    │
    ├── FPA Results
    │   ├── Total Function Points
    │   ├── Adjusted Function Points
    │   └── FPA Effort (person-months)
    │
    ├── Cost Estimation
    │   ├── Total Cost (USD)
    │   └── Average Cost per Month
    │
    ├── Manpower Distribution
    │   ├── Team Composition
    │   │   └── All Roles with Counts
    │   ├── Phase-Based Effort
    │   │   └── All Phases with Effort
    │   └── Recommendations
    │
    ├── Technology Stack
    │   ├── Frontend Technologies
    │   ├── Backend Technologies
    │   ├── Databases
    │   ├── Cloud Platforms
    │   ├── DevOps Tools
    │   ├── Testing Frameworks
    │   ├── Reasoning
    │   └── Considerations
    │
    ├── Risk Assessment
    │   ├── Risk Level
    │   └── Risk Factors
    │
    └── Overall Recommendations
```

---

## 🔧 Service Dependencies

```
ProjectAnalysisService
    │
    ├── depends on → COCOMOService
    │                   └── uses ProjectRequirements
    │                   └── returns effort, time, staff size
    │
    ├── depends on → FPAService
    │                   └── uses ProjectRequirements
    │                   └── returns function points, effort
    │
    ├── depends on → TechnologyRecommendationService
    │                   └── uses ProjectRequirements
    │                   └── returns TechnologyRecommendation
    │
    └── depends on → ManpowerAnalysisService
                        └── uses ProjectRequirements + effort + time
                        └── returns ManpowerDistribution
```

---

## 🎯 Key Algorithms

### COCOMO Calculation
```
Input: KLOC, ProjectType, Complexity Factors

Step 1: Select constants based on ProjectType
        Organic:       a=2.4, b=1.05, c=2.5, d=0.38
        Semi-Detached: a=3.0, b=1.12, c=2.5, d=0.35
        Embedded:      a=3.6, b=1.20, c=2.5, d=0.32

Step 2: Calculate base effort
        Effort = a × (KLOC)^b

Step 3: Apply complexity multiplier
        multiplier = 1.0
        + (complexityLevel - 3) × 0.1
        + (highSecurity ? 0.15 : 0)
        + (highPerformance ? 0.12 : 0)
        + (scalability ? 0.10 : 0)
        + (realTime ? 0.18 : 0)
        
        Effort = Effort × multiplier

Step 4: Calculate development time
        TDEV = c × (Effort)^d

Step 5: Calculate average staff
        Staff = Effort / TDEV

Output: Effort, TDEV, Staff, Productivity
```

### Function Point Analysis
```
Input: Inputs, Outputs, Inquiries, Files, Interfaces

Step 1: Calculate unadjusted function points
        UFP = (Inputs × 4) + (Outputs × 5) + 
              (Inquiries × 4) + (Files × 10) + 
              (Interfaces × 7)

Step 2: Calculate technical complexity factor
        TCF = sum of 14 influence factors (0-5 each)
        
Step 3: Calculate value adjustment factor
        VAF = 0.65 + (0.01 × TCF)

Step 4: Calculate adjusted function points
        AFP = UFP × VAF

Step 5: Calculate effort
        Effort = AFP / 10 (productivity factor)

Output: UFP, AFP, Effort
```

### Manpower Distribution
```
Input: Effort, Time, ProjectRequirements

Step 1: Calculate total team size
        TeamSize = ceil(Effort / Time)
        if TeamSize > 10: TeamSize × 1.15 (Brooks' Law)

Step 2: Distribute roles
        Developers = TeamSize × 0.6
        ProjectManagers = max(1, TeamSize / 10)
        TechLeads = max(1, Developers / 6)
        
        Based on complexity:
        - High: 40% Senior, 35% Mid, 25% Junior
        - Medium: 30% Senior, 40% Mid, 30% Junior
        - Low: 25% Senior, 35% Mid, 40% Junior
        
        QA = max(1, Developers / 4)
        DevOps = based on scalability needs
        UI/UX = based on domain
        BA = max(1, TeamSize / 12)

Step 3: Distribute effort by phase
        Requirements: 10-12%
        Design: 15-18%
        Development: 45-50%
        Testing: 20-25%
        Deployment: 5%

Output: ManpowerDistribution
```

---

## 🚀 Request/Response Example

### HTTP Request
```http
POST /api/analysis/analyze HTTP/1.1
Content-Type: application/json

{
  "projectName": "E-Commerce Platform",
  "domain": "WEB_APPLICATION",
  "projectType": "SEMI_DETACHED",
  "estimatedKLOC": 50.0,
  "expectedUsers": 10000,
  "durationMonths": 12,
  "complexityLevel": 3,
  "externalInputs": 20,
  "externalOutputs": 15,
  "externalInquiries": 10,
  "internalFiles": 8,
  "externalInterfaces": 5,
  "requiresHighSecurity": true,
  "requiresHighPerformance": true,
  "requiresScalability": true,
  "requiresRealTime": false
}
```

### HTTP Response
```http
HTTP/1.1 200 OK
Content-Type: application/json

{
  "projectName": "E-Commerce Platform",
  "domain": "WEB_APPLICATION",
  "projectType": "SEMI_DETACHED",
  "effortPersonMonths": 195.5,
  "developmentTimeMonths": 16.8,
  "averageStaffSize": 11.6,
  "productivityLOCPerPersonMonth": 255.8,
  "totalFunctionPoints": 285,
  "adjustedFunctionPoints": 312.5,
  "fpaEffortPersonMonths": 31.3,
  "estimatedCostUSD": 1134000,
  "manpowerDistribution": { ... },
  "technologyRecommendation": { ... },
  "riskLevel": "MEDIUM",
  "riskFactors": "...",
  "recommendations": "..."
}
```

---

This system flow provides a complete picture of how the application processes project requirements and generates comprehensive analysis results.
