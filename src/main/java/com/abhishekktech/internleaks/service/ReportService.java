package com.abhishekktech.internleaks.service;

import java.util.List;
import java.util.Optional;

import com.abhishekktech.internleaks.entity.Company;
import com.abhishekktech.internleaks.entity.Report;
import com.abhishekktech.internleaks.repository.CompanyRepository;
import com.abhishekktech.internleaks.repository.ReportRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReportService {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private ReportRepository reportRepository;

    // 🔥 FIX: Yahan saare naye fields add kar diye hain
    public Report submitReport(String companyName, String websiteUrl, String scamType, String description,
                               Integer riskPercentage, String verdict, String redFlags, 
                               String hrEmailDomain, String paymentDemanded, String interviewTaken) {

        Optional<Company> existingCompany = companyRepository.findByNameIgnoreCase(companyName);

        Company company;

        if (existingCompany.isPresent()) {
            company = existingCompany.get();
        } else {
            company = new Company();
            company.setName(companyName);
            company.setWebsiteUrl(websiteUrl);
            company = companyRepository.save(company);
        }

        Report report = new Report();
        report.setCompany(company);
        report.setScamType(scamType);
        report.setDescription(description);
        
        // 🔥 FIX: Naye AI fields ko Database Entity mein set kar rahe hain
        report.setRiskPercentage(riskPercentage);
        report.setVerdict(verdict);
        report.setRedFlags(redFlags);
        report.setCompanyWebsite(websiteUrl);
        report.setHrEmailDomain(hrEmailDomain);
        report.setPaymentDemanded(paymentDemanded);
        report.setInterviewTaken(interviewTaken);

        return reportRepository.save(report);
    }

    public List<Company> searchCompanies(String query) {
        return companyRepository.findByNameContainingIgnoreCase(query);
    }

    public List<Report> getReportsByCompany(Long companyId) {
        return reportRepository.findByCompanyIdOrderByCreatedAtDesc(companyId);
    }

    public List<Company> getAllCompanies() {
        return companyRepository.findAll();
    }
}