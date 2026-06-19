package com.abhishekktech.internleaks.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "scam_reports")
public class ScamReport {

    @Column(name = "user_email")
    private String userEmail;

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String companyName;
    private Integer riskPercentage;

    @Column(columnDefinition = "TEXT")
    private String verdict;

    @Column(columnDefinition = "TEXT")
    private String redFlags; // JSON string ya comma-separated list ke liye

    private LocalDateTime reportedAt;

    // Database mein save hone se pehle time auto-set karne ke liye
    @PrePersist
    protected void onCreate() {
        reportedAt = LocalDateTime.now();
    }

    // --- Getters & Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public Integer getRiskPercentage() { return riskPercentage; }
    public void setRiskPercentage(Integer riskPercentage) { this.riskPercentage = riskPercentage; }

    public String getVerdict() { return verdict; }
    public void setVerdict(String verdict) { this.verdict = verdict; }

    public String getRedFlags() { return redFlags; }
    public void setRedFlags(String redFlags) { this.redFlags = redFlags; }

    public LocalDateTime getReportedAt() { return reportedAt; }
    public void setReportedAt(LocalDateTime reportedAt) { this.reportedAt = reportedAt; }
}