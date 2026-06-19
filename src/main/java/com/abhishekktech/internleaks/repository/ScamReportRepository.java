package com.abhishekktech.internleaks.repository;

import com.abhishekktech.internleaks.entity.ScamReport;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ScamReportRepository extends JpaRepository<ScamReport, Long> {
    List<ScamReport> findByUserEmail(String userEmail); // Email se AI Scan History nikalne ke liye
}