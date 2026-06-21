package com.abhishekktech.internleaks.repository;

import com.abhishekktech.internleaks.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {
    
    // Matches ReportService usage
    List<Report> findByCompanyIdOrderByCreatedAtDesc(Long companyId);
    
    // For the user dashboard
    List<Report> findByUserEmail(String userEmail); 
}