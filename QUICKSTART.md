# Quick Start Guide

## 🚀 Get Started in 3 Steps

### Step 1: Build the Application

```bash
./mvnw clean install
```

### Step 2: Run the Application

```bash
./mvnw spring-boot:run
```

### Step 3: Open in Browser

Navigate to: **http://localhost:8080**

---

## 📝 Sample Project Analysis

Try analyzing a sample project with these values:

### Example 1: E-Commerce Web Application

- **Project Name:** E-Commerce Platform
- **Domain:** Web Application
- **Project Type:** Semi-Detached
- **Estimated KLOC:** 50
- **Expected Users:** 10,000
- **Duration:** 12 months
- **Complexity Level:** 3 (Average)
- **Function Points:**
  - External Inputs: 20
  - External Outputs: 15
  - External Inquiries: 10
  - Internal Files: 8
  - External Interfaces: 5
- **Requirements:**
  - ✅ High Security
  - ✅ High Performance
  - ✅ Scalability
  - ❌ Real-Time

**Expected Results:**
- Team Size: ~8-12 members
- Development Time: ~15-18 months
- Cost: ~$400,000-$600,000
- Tech Stack: React/Angular, Node.js/Spring Boot, PostgreSQL, AWS/Azure

---

### Example 2: Mobile Banking App

- **Project Name:** Mobile Banking
- **Domain:** Mobile Application
- **Project Type:** Embedded
- **Estimated KLOC:** 30
- **Expected Users:** 50,000
- **Duration:** 10 months
- **Complexity Level:** 5 (Very High)
- **Function Points:**
  - External Inputs: 25
  - External Outputs: 20
  - External Inquiries: 15
  - Internal Files: 10
  - External Interfaces: 8
- **Requirements:**
  - ✅ High Security
  - ✅ High Performance
  - ✅ Scalability
  - ✅ Real-Time

**Expected Results:**
- Team Size: ~10-15 members
- Development Time: ~12-16 months
- Cost: ~$500,000-$800,000
- Tech Stack: React Native/Flutter, Node.js/Spring Boot, PostgreSQL, AWS

---

### Example 3: Simple Internal Tool

- **Project Name:** Employee Directory
- **Domain:** Web Application
- **Project Type:** Organic
- **Estimated KLOC:** 5
- **Expected Users:** 500
- **Duration:** 3 months
- **Complexity Level:** 1 (Very Low)
- **Function Points:**
  - External Inputs: 5
  - External Outputs: 5
  - External Inquiries: 3
  - Internal Files: 2
  - External Interfaces: 1
- **Requirements:**
  - ❌ High Security
  - ❌ High Performance
  - ❌ Scalability
  - ❌ Real-Time

**Expected Results:**
- Team Size: ~3-5 members
- Development Time: ~4-6 months
- Cost: ~$50,000-$100,000
- Tech Stack: React/Vue.js, Node.js/Django, PostgreSQL/MySQL

---

## 🎯 Understanding the Results

### COCOMO Estimation
- **Effort:** Total person-months required
- **Development Time:** Calendar months for completion
- **Average Staff Size:** Recommended team size
- **Productivity:** Lines of code per person-month

### Function Point Analysis
- **Total Function Points:** Raw functionality measure
- **Adjusted Function Points:** Complexity-adjusted measure
- **FPA Effort:** Alternative effort estimation

### Manpower Distribution
- Shows recommended team composition
- Breaks down effort by project phase
- Provides staffing recommendations

### Technology Stack
- Recommends appropriate technologies
- Explains reasoning behind choices
- Lists important considerations

### Risk Assessment
- Identifies risk level (Low/Medium/High/Very High)
- Lists specific risk factors
- Helps in risk mitigation planning

---

## 💡 Tips for Accurate Estimation

1. **KLOC Estimation:** Use historical data or count similar projects
2. **Function Points:** Be thorough in counting all inputs/outputs/files
3. **Complexity Level:** Be realistic about technical challenges
4. **Requirements:** Check all applicable special requirements
5. **Project Type:**
   - **Organic:** Small, familiar, low-risk projects
   - **Semi-Detached:** Medium complexity, some new challenges
   - **Embedded:** High complexity, tight constraints, critical systems

---

## 🔧 Troubleshooting

### Application won't start
- Ensure Java 17+ is installed: `java -version`
- Check port 8080 is available
- Review logs for error messages

### Build fails
- Ensure Maven is working: `./mvnw -version`
- Clear Maven cache: `./mvnw clean`
- Check internet connection (for dependencies)

### Results seem inaccurate
- Verify input values are realistic
- Check project type matches your scenario
- Consider using both COCOMO and FPA results
- Remember: These are estimates, not guarantees

---

## 📚 Next Steps

1. Read the full [README.md](README.md) for detailed documentation
2. Visit the [About page](http://localhost:8080/about) in the application
3. Experiment with different project scenarios
4. Compare estimates with actual project data
5. Adjust parameters based on your organization's context

---

**Happy Estimating! 📊**
