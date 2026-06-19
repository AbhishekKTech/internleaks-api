package com.abhishekktech.internleaks.repository;

import com.abhishekktech.internleaks.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {
    
    // Ye function ab tere ReportService se match karega
    List<Report> findByCompanyIdOrderByCreatedAtDesc(Long companyId);
    
    // User ke dashboard ke liye
    List<Report> findByUserEmail(String userEmail); 
}