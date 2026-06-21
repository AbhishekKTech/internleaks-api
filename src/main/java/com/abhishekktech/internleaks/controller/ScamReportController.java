package com.abhishekktech.internleaks.controller;

import com.abhishekktech.internleaks.entity.ScamReport;
import com.abhishekktech.internleaks.repository.ScamReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reports")
@CrossOrigin(origins = "http://localhost:3000")
public class ScamReportController {

    @Autowired
    private ScamReportRepository repository;

    // 1. Save a new scam report (POST)
    @PostMapping
    public ResponseEntity<ScamReport> saveReport(@RequestBody ScamReport report) {
        ScamReport savedReport = repository.save(report);
        return ResponseEntity.ok(savedReport);
    }

    // 2. Retrieve all scam reports (GET)
    @GetMapping
    public ResponseEntity<List<ScamReport>> getAllReports() {
        return ResponseEntity.ok(repository.findAll());
    }
}