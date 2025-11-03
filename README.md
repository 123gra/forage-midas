# Software Project Analysis System

## 📊 Overview

A comprehensive web application for analyzing manpower and technology requirements in software development projects. This decision-support system uses industry-standard estimation models (COCOMO and Function Point Analysis) to provide evidence-based recommendations for staffing and technology stack selection.

## 🎯 Purpose

Software development requires careful planning of resources in terms of both manpower and technology. This system addresses the common problem of inaccurate estimations by providing:

- **Automated Effort Estimation** using COCOMO (Constructive Cost Model)
- **Function Point Analysis** for complexity assessment
- **Manpower Distribution** recommendations across different roles
- **Technology Stack Recommendations** based on project requirements
- **Risk Assessment** and mitigation strategies
- **Cost Estimation** for budget planning

## 🚀 Features

### 1. COCOMO Model Implementation
- Supports three project types: Organic, Semi-Detached, and Embedded
- Calculates effort (person-months), development time, and team size
- Adjusts for complexity factors and special requirements

### 2. Function Point Analysis (FPA)
- Measures software size based on functionality
- Considers external inputs, outputs, inquiries, files, and interfaces
- Applies value adjustment factors for technical complexity

### 3. Manpower Analysis
- Recommends optimal team size and composition
- Distributes roles: Project Managers, Tech Leads, Developers (Senior/Mid/Junior), QA, DevOps, UI/UX, Business Analysts
- Provides phase-based effort distribution (Requirements, Design, Development, Testing, Deployment)

### 4. Technology Recommendation Engine
- Rule-based recommendations for:
  - Frontend technologies
  - Backend frameworks
  - Databases
  - Cloud platforms
  - DevOps tools
  - Testing frameworks
- Considers project domain, scale, performance, security, and scalability requirements

### 5. Risk Assessment
- Evaluates project risks based on size, complexity, team size, and requirements
- Provides risk level classification (Low, Medium, High, Very High)
- Identifies specific risk factors

## 🛠️ Technology Stack

- **Backend:** Spring Boot 3.2.5 (Java 17)
- **Frontend:** HTML5, CSS3, JavaScript (Vanilla)
- **Templating:** Thymeleaf
- **Build Tool:** Maven
- **Dependencies:** Spring Web, Spring Validation, Lombok

## 📋 Prerequisites

- Java 17 or higher
- Maven 3.6+ (or use the included Maven wrapper)

## 🔧 Installation & Setup

### 1. Clone or Download the Project

```bash
cd /path/to/project
```

### 2. Build the Project

Using Maven wrapper (recommended):
```bash
./mvnw clean install
```

Or using system Maven:
```bash
mvn clean install
```

### 3. Run the Application

Using Maven wrapper:
```bash
./mvnw spring-boot:run
```

Or using system Maven:
```bash
mvn spring-boot:run
```

Or run the JAR directly:
```bash
java -jar target/midas-core-1.0.0.jar
```

### 4. Access the Application

Open your web browser and navigate to:
```
http://localhost:8080
```

## 📖 Usage Guide

### Step 1: Enter Project Requirements

Fill in the project analysis form with the following information:

**Basic Information:**
- Project Name
- Project Domain (Web, Mobile, Enterprise, etc.)
- Project Type (Organic, Semi-Detached, Embedded)
- Estimated KLOC (Thousands of Lines of Code)
- Expected Users
- Duration in Months
- Complexity Level (1-5)

**Function Point Analysis:**
- External Inputs
- External Outputs
- External Inquiries
- Internal Files
- External Interfaces

**Additional Requirements:**
- High Security
- High Performance
- Scalability
- Real-Time Processing

### Step 2: Analyze

Click the "Analyze Project" button to process the requirements.

### Step 3: Review Results

The system will display:
- **Project Summary:** Basic project information
- **COCOMO Estimation:** Effort, development time, staff size, productivity
- **Function Point Analysis:** Function points and FPA-based effort
- **Cost Estimation:** Estimated project cost in USD
- **Manpower Distribution:** Team composition and phase-based effort
- **Technology Recommendations:** Suggested tech stack with reasoning
- **Risk Assessment:** Risk level and factors
- **Overall Recommendations:** Strategic guidance for project success

## 🏗️ Project Structure

```
src/
├── main/
│   ├── java/com/jpmc/midascore/
│   │   ├── MidasCoreApplication.java          # Main application class
│   │   ├── controller/
│   │   │   ├── AnalysisController.java        # REST API endpoints
│   │   │   └── WebController.java             # Web page controllers
│   │   ├── model/
│   │   │   ├── ProjectRequirements.java       # Input model
│   │   │   ├── EstimationResult.java          # Output model
│   │   │   ├── ManpowerDistribution.java      # Manpower details
│   │   │   ├── TechnologyRecommendation.java  # Tech stack details
│   │   │   ├── ProjectType.java               # Enum for project types
│   │   │   └── ProjectDomain.java             # Enum for domains
│   │   └── service/
│   │       ├── COCOMOService.java             # COCOMO calculations
│   │       ├── FPAService.java                # Function Point Analysis
│   │       ├── TechnologyRecommendationService.java
│   │       ├── ManpowerAnalysisService.java
│   │       └── ProjectAnalysisService.java    # Main orchestration
│   └── resources/
│       ├── application.yml                     # Application configuration
│       ├── templates/
│       │   ├── index.html                      # Main analysis page
│       │   └── about.html                      # About page
│       └── static/
│           ├── css/
│           │   └── styles.css                  # Application styles
│           └── js/
│               └── app.js                      # Frontend logic
└── test/
    └── java/                                   # Test files
```

## 🔌 API Endpoints

### POST /api/analysis/analyze
Analyzes project requirements and returns comprehensive estimation results.

**Request Body:**
```json
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

**Response:** EstimationResult object with all analysis details

### GET /api/analysis/health
Health check endpoint

## 📊 Estimation Models Explained

### COCOMO (Constructive Cost Model)

COCOMO uses the following formulas:

**Effort (Person-Months):**
```
Effort = a × (KLOC)^b × ComplexityMultiplier
```

**Development Time (Months):**
```
TDEV = c × (Effort)^d
```

**Constants by Project Type:**
- **Organic:** a=2.4, b=1.05, c=2.5, d=0.38
- **Semi-Detached:** a=3.0, b=1.12, c=2.5, d=0.35
- **Embedded:** a=3.6, b=1.20, c=2.5, d=0.32

### Function Point Analysis (FPA)

**Unadjusted Function Points:**
```
UFP = (Inputs × 4) + (Outputs × 5) + (Inquiries × 4) + 
      (Files × 10) + (Interfaces × 7)
```

**Adjusted Function Points:**
```
AFP = UFP × VAF
VAF = 0.65 + (0.01 × TotalInfluence)
```

**Effort from FPA:**
```
Effort = AFP / 10 (assuming 10 FP per person-month)
```

## 🎨 User Interface

The application features a modern, responsive design with:
- Clean, professional layout
- Intuitive form inputs with validation
- Real-time analysis with loading indicators
- Comprehensive results display with visual hierarchy
- Mobile-responsive design
- Accessible color scheme and typography

## 🧪 Testing

To run tests:
```bash
./mvnw test
```

## 📝 Configuration

Application configuration is in `src/main/resources/application.yml`:

```yaml
server:
  port: 8080  # Change port if needed

spring:
  application:
    name: midas-core
```

## 🤝 Contributing

This is an educational project demonstrating software estimation techniques. Contributions for improvements are welcome.

## 📄 License

This project is part of the JPMC Advanced Software Engineering Forage program.

## 🔗 References

- **COCOMO Model:** Barry W. Boehm, "Software Engineering Economics" (1981)
- **Function Point Analysis:** Allan J. Albrecht, IBM (1979)
- **Software Estimation Best Practices:** Steve McConnell, "Software Estimation: Demystifying the Black Art"

## 📞 Support

For issues or questions, please refer to the project documentation or contact the development team.

## 🎓 Educational Purpose

This system is designed for educational purposes to demonstrate:
- Software estimation techniques
- Spring Boot application development
- RESTful API design
- Modern web application architecture
- Evidence-based decision support systems

---

**Built with ❤️ using Spring Boot and modern web technologies**
