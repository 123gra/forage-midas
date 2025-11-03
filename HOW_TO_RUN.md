# 🚀 How to Run the Application

## Prerequisites
- Java 17 or higher installed
- Maven (or use the included Maven wrapper)

## Quick Start (3 Commands)

### 1️⃣ Build the Application
```bash
./mvnw clean install
```

### 2️⃣ Run the Application
```bash
./mvnw spring-boot:run
```

### 3️⃣ Open in Browser
```
http://localhost:8080
```

---

## That's It! 🎉

The application will start on port 8080. You'll see:
- Main analysis page at: `http://localhost:8080/`
- About page at: `http://localhost:8080/about`

---

## What to Do Next

1. **Fill in the form** with your project details
2. **Click "Analyze Project"** button
3. **Review the results** including:
   - COCOMO estimation
   - Function Point Analysis
   - Manpower distribution
   - Technology recommendations
   - Risk assessment
   - Cost estimation

---

## Sample Data to Try

**Project Name:** E-Commerce Platform  
**Domain:** Web Application  
**Project Type:** Semi-Detached  
**Estimated KLOC:** 50  
**Expected Users:** 10000  
**Duration:** 12 months  
**Complexity Level:** 3 (Average)  

**Function Points:**
- External Inputs: 20
- External Outputs: 15
- External Inquiries: 10
- Internal Files: 8
- External Interfaces: 5

**Check these boxes:**
- ✅ High Security
- ✅ High Performance
- ✅ Scalability

Click "Analyze Project" and see the comprehensive results!

---

## Troubleshooting

### Port 8080 already in use?
Change the port in `src/main/resources/application.yml`:
```yaml
server:
  port: 8081  # or any other available port
```

### Java not found?
Install Java 17 or higher:
- **Ubuntu/Debian:** `sudo apt install openjdk-17-jdk`
- **macOS:** `brew install openjdk@17`
- **Windows:** Download from [Oracle](https://www.oracle.com/java/technologies/downloads/) or [Adoptium](https://adoptium.net/)

### Build fails?
Make sure you have internet connection (Maven needs to download dependencies).

---

## Need More Help?

- 📖 Read [README.md](README.md) for detailed documentation
- 🚀 Check [QUICKSTART.md](QUICKSTART.md) for examples
- 📊 See [PROJECT_SUMMARY.md](PROJECT_SUMMARY.md) for technical details
- 🔄 Review [SYSTEM_FLOW.md](SYSTEM_FLOW.md) for architecture

---

**Enjoy analyzing your software projects! 📊✨**
