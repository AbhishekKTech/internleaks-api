package com.abhishekktech.internleaks.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reports")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    private String scamType;

    @Column(columnDefinition = "TEXT")
    private String description;

    // New columns for AI data analysis
    private Integer riskPercentage;

    @Column(columnDefinition = "TEXT")
    private String verdict;

    @Column(columnDefinition = "TEXT")
    private String redFlags; 

    @Column(name = "user_email")
    private String userEmail;

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "is_anonymous")
    private boolean isAnonymous = true;

    private String companyWebsite;
    private String hrEmailDomain;
    private String paymentDemanded;
    private String interviewTaken;

    // --- Getters and Setters ---
    public boolean isAnonymous() { return isAnonymous; }
    public void setAnonymous(boolean isAnonymous) { this.isAnonymous = isAnonymous; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Company getCompany() { return company; }
    public void setCompany(Company company) { this.company = company; }

    public String getScamType() { return scamType; }
    public void setScamType(String scamType) { this.scamType = scamType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getRiskPercentage() { return riskPercentage; }
    public void setRiskPercentage(Integer riskPercentage) { this.riskPercentage = riskPercentage; }

    public String getVerdict() { return verdict; }
    public void setVerdict(String verdict) { this.verdict = verdict; }

    public String getRedFlags() { return redFlags; }
    public void setRedFlags(String redFlags) { this.redFlags = redFlags; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }


    public String getCompanyWebsite() { return companyWebsite; }
    public void setCompanyWebsite(String companyWebsite) { this.companyWebsite = companyWebsite; }

    public String getHrEmailDomain() { return hrEmailDomain; }
    public void setHrEmailDomain(String hrEmailDomain) { this.hrEmailDomain = hrEmailDomain; }

    public String getPaymentDemanded() { return paymentDemanded; }
    public void setPaymentDemanded(String paymentDemanded) { this.paymentDemanded = paymentDemanded; }

    public String getInterviewTaken() { return interviewTaken; }
    public void setInterviewTaken(String interviewTaken) { this.interviewTaken = interviewTaken; }
}