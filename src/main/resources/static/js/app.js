document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('analysisForm');
    const loadingIndicator = document.getElementById('loadingIndicator');
    const resultsSection = document.getElementById('resultsSection');

    form.addEventListener('submit', async function(e) {
        e.preventDefault();
        
        // Collect form data
        const formData = {
            projectName: document.getElementById('projectName').value,
            domain: document.getElementById('domain').value,
            projectType: document.getElementById('projectType').value,
            estimatedKLOC: parseFloat(document.getElementById('estimatedKLOC').value),
            expectedUsers: parseInt(document.getElementById('expectedUsers').value),
            durationMonths: parseInt(document.getElementById('durationMonths').value),
            complexityLevel: parseInt(document.getElementById('complexityLevel').value),
            externalInputs: parseInt(document.getElementById('externalInputs').value) || 0,
            externalOutputs: parseInt(document.getElementById('externalOutputs').value) || 0,
            externalInquiries: parseInt(document.getElementById('externalInquiries').value) || 0,
            internalFiles: parseInt(document.getElementById('internalFiles').value) || 0,
            externalInterfaces: parseInt(document.getElementById('externalInterfaces').value) || 0,
            requiresHighSecurity: document.getElementById('requiresHighSecurity').checked,
            requiresHighPerformance: document.getElementById('requiresHighPerformance').checked,
            requiresScalability: document.getElementById('requiresScalability').checked,
            requiresRealTime: document.getElementById('requiresRealTime').checked
        };

        // Show loading indicator
        loadingIndicator.style.display = 'block';
        resultsSection.style.display = 'none';

        try {
            const response = await fetch('/api/analysis/analyze', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(formData)
            });

            if (!response.ok) {
                throw new Error('Analysis failed');
            }

            const result = await response.json();
            displayResults(result);
            
            // Hide loading, show results
            loadingIndicator.style.display = 'none';
            resultsSection.style.display = 'block';
            
            // Scroll to results
            resultsSection.scrollIntoView({ behavior: 'smooth' });
        } catch (error) {
            console.error('Error:', error);
            alert('An error occurred during analysis. Please try again.');
            loadingIndicator.style.display = 'none';
        }
    });

    function displayResults(result) {
        // Project Summary
        document.getElementById('projectSummary').innerHTML = `
            <div class="result-item">
                <span class="result-label">Project Name:</span>
                <span class="result-value">${result.projectName}</span>
            </div>
            <div class="result-item">
                <span class="result-label">Domain:</span>
                <span class="result-value">${formatEnum(result.domain)}</span>
            </div>
            <div class="result-item">
                <span class="result-label">Project Type:</span>
                <span class="result-value">${formatEnum(result.projectType)}</span>
            </div>
        `;

        // COCOMO Results
        document.getElementById('cocomoResults').innerHTML = `
            <div class="result-item">
                <span class="result-label">Effort:</span>
                <span class="result-value">${result.effortPersonMonths.toFixed(2)} person-months</span>
            </div>
            <div class="result-item">
                <span class="result-label">Development Time:</span>
                <span class="result-value">${result.developmentTimeMonths.toFixed(2)} months</span>
            </div>
            <div class="result-item">
                <span class="result-label">Average Staff Size:</span>
                <span class="result-value">${result.averageStaffSize.toFixed(1)} people</span>
            </div>
            <div class="result-item">
                <span class="result-label">Productivity:</span>
                <span class="result-value">${result.productivityLOCPerPersonMonth.toFixed(0)} LOC/PM</span>
            </div>
        `;

        // FPA Results
        document.getElementById('fpaResults').innerHTML = `
            <div class="result-item">
                <span class="result-label">Total Function Points:</span>
                <span class="result-value">${result.totalFunctionPoints}</span>
            </div>
            <div class="result-item">
                <span class="result-label">Adjusted Function Points:</span>
                <span class="result-value">${result.adjustedFunctionPoints.toFixed(2)}</span>
            </div>
            <div class="result-item">
                <span class="result-label">FPA Effort:</span>
                <span class="result-value">${result.fpaEffortPersonMonths.toFixed(2)} person-months</span>
            </div>
        `;

        // Cost Results
        document.getElementById('costResults').innerHTML = `
            <div class="result-item">
                <span class="result-label">Estimated Cost:</span>
                <span class="result-value">$${result.estimatedCostUSD.toLocaleString('en-US', {maximumFractionDigits: 0})}</span>
            </div>
            <div class="result-item">
                <span class="result-label">Average Cost/Month:</span>
                <span class="result-value">$${(result.estimatedCostUSD / result.developmentTimeMonths).toLocaleString('en-US', {maximumFractionDigits: 0})}</span>
            </div>
        `;

        // Manpower Distribution
        const manpower = result.manpowerDistribution;
        document.getElementById('manpowerResults').innerHTML = `
            <div style="margin-bottom: 1.5rem;">
                <h4 style="color: var(--primary-color); margin-bottom: 1rem;">Team Composition</h4>
                <div class="result-item">
                    <span class="result-label">Total Team Size:</span>
                    <span class="result-value">${manpower.totalTeamSize} members</span>
                </div>
                <div class="result-item">
                    <span class="result-label">Project Managers:</span>
                    <span class="result-value">${manpower.projectManagers}</span>
                </div>
                <div class="result-item">
                    <span class="result-label">Tech Leads:</span>
                    <span class="result-value">${manpower.techLeads}</span>
                </div>
                <div class="result-item">
                    <span class="result-label">Senior Developers:</span>
                    <span class="result-value">${manpower.seniorDevelopers}</span>
                </div>
                <div class="result-item">
                    <span class="result-label">Mid-Level Developers:</span>
                    <span class="result-value">${manpower.midLevelDevelopers}</span>
                </div>
                <div class="result-item">
                    <span class="result-label">Junior Developers:</span>
                    <span class="result-value">${manpower.juniorDevelopers}</span>
                </div>
                <div class="result-item">
                    <span class="result-label">QA Engineers:</span>
                    <span class="result-value">${manpower.qaEngineers}</span>
                </div>
                <div class="result-item">
                    <span class="result-label">DevOps Engineers:</span>
                    <span class="result-value">${manpower.devOpsEngineers}</span>
                </div>
                <div class="result-item">
                    <span class="result-label">UI/UX Designers:</span>
                    <span class="result-value">${manpower.uiUxDesigners}</span>
                </div>
                <div class="result-item">
                    <span class="result-label">Business Analysts:</span>
                    <span class="result-value">${manpower.businessAnalysts}</span>
                </div>
            </div>
            <div>
                <h4 style="color: var(--primary-color); margin-bottom: 1rem;">Phase-Based Effort Distribution</h4>
                <div class="result-item">
                    <span class="result-label">Requirements Analysis:</span>
                    <span class="result-value">${manpower.requirementsAnalysisEffort.toFixed(2)} PM</span>
                </div>
                <div class="result-item">
                    <span class="result-label">Design:</span>
                    <span class="result-value">${manpower.designEffort.toFixed(2)} PM</span>
                </div>
                <div class="result-item">
                    <span class="result-label">Development:</span>
                    <span class="result-value">${manpower.developmentEffort.toFixed(2)} PM</span>
                </div>
                <div class="result-item">
                    <span class="result-label">Testing:</span>
                    <span class="result-value">${manpower.testingEffort.toFixed(2)} PM</span>
                </div>
                <div class="result-item">
                    <span class="result-label">Deployment:</span>
                    <span class="result-value">${manpower.deploymentEffort.toFixed(2)} PM</span>
                </div>
            </div>
            <div style="margin-top: 1.5rem; padding: 1rem; background: var(--bg-color); border-radius: 8px;">
                <strong>Manpower Recommendations:</strong>
                <p style="margin-top: 0.5rem; color: var(--text-secondary);">${manpower.recommendations}</p>
            </div>
        `;

        // Technology Recommendations
        const tech = result.technologyRecommendation;
        document.getElementById('technologyResults').innerHTML = `
            <div style="margin-bottom: 1rem;">
                <h4 style="color: var(--primary-dark); margin-bottom: 0.5rem;">Frontend Technologies</h4>
                <div class="tech-list">
                    ${tech.frontendTechnologies.map(t => `<span class="tech-badge">${t}</span>`).join('')}
                </div>
            </div>
            <div style="margin-bottom: 1rem;">
                <h4 style="color: var(--primary-dark); margin-bottom: 0.5rem;">Backend Technologies</h4>
                <div class="tech-list">
                    ${tech.backendTechnologies.map(t => `<span class="tech-badge">${t}</span>`).join('')}
                </div>
            </div>
            <div style="margin-bottom: 1rem;">
                <h4 style="color: var(--primary-dark); margin-bottom: 0.5rem;">Databases</h4>
                <div class="tech-list">
                    ${tech.databases.map(t => `<span class="tech-badge">${t}</span>`).join('')}
                </div>
            </div>
            <div style="margin-bottom: 1rem;">
                <h4 style="color: var(--primary-dark); margin-bottom: 0.5rem;">Cloud Platforms</h4>
                <div class="tech-list">
                    ${tech.cloudPlatforms.map(t => `<span class="tech-badge">${t}</span>`).join('')}
                </div>
            </div>
            <div style="margin-bottom: 1rem;">
                <h4 style="color: var(--primary-dark); margin-bottom: 0.5rem;">DevOps Tools</h4>
                <div class="tech-list">
                    ${tech.devOpsTools.map(t => `<span class="tech-badge">${t}</span>`).join('')}
                </div>
            </div>
            <div style="margin-bottom: 1rem;">
                <h4 style="color: var(--primary-dark); margin-bottom: 0.5rem;">Testing Frameworks</h4>
                <div class="tech-list">
                    ${tech.testingFrameworks.map(t => `<span class="tech-badge">${t}</span>`).join('')}
                </div>
            </div>
            <div style="margin-top: 1.5rem; padding: 1rem; background: var(--bg-color); border-radius: 8px;">
                <strong>Reasoning:</strong>
                <p style="margin-top: 0.5rem; color: var(--text-secondary);">${tech.reasoning}</p>
            </div>
            <div style="margin-top: 1rem; padding: 1rem; background: var(--bg-color); border-radius: 8px;">
                <strong>Considerations:</strong>
                <ul style="margin-left: 1.5rem; margin-top: 0.5rem;">
                    ${tech.considerations.map(c => `<li style="color: var(--text-secondary);">${c}</li>`).join('')}
                </ul>
            </div>
        `;

        // Risk Assessment
        document.getElementById('riskResults').innerHTML = `
            <div class="result-item">
                <span class="result-label">Risk Level:</span>
                <span class="risk-badge risk-${result.riskLevel.replace(' ', '.')}">${result.riskLevel}</span>
            </div>
            <div style="margin-top: 1rem; padding: 1rem; background: var(--bg-color); border-radius: 8px;">
                <strong>Risk Factors:</strong>
                <p style="margin-top: 0.5rem; color: var(--text-secondary);">${result.riskFactors || 'No significant risk factors identified.'}</p>
            </div>
        `;

        // Overall Recommendations
        document.getElementById('recommendationsResults').innerHTML = `
            <p style="color: var(--text-secondary); line-height: 1.8;">${result.recommendations}</p>
        `;
    }

    function formatEnum(value) {
        return value.replace(/_/g, ' ').replace(/\b\w/g, l => l.toUpperCase());
    }
});
