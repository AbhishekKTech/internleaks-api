package com.abhishekktech.internleaks.controller;

import com.abhishekktech.internleaks.entity.Company;
import com.abhishekktech.internleaks.entity.Report;
import com.abhishekktech.internleaks.entity.ScamReport;
import com.abhishekktech.internleaks.repository.ReportRepository;
import com.abhishekktech.internleaks.repository.ScamReportRepository;
import com.abhishekktech.internleaks.service.AiAnalysisService;
import com.abhishekktech.internleaks.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "*")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @Autowired
    private AiAnalysisService aiAnalysisService;

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private ScamReportRepository scamReportRepository;

    // ==========================================
    // 1. SCAM WALL APIs
    // ==========================================



    // ==========================================
    // 1. SCAM WALL APIs
    // ==========================================
    
    // 👉 NAYA ENDPOINT: Scam Wall ke liye saari public reports nikalne ke liye
    @GetMapping("/reports/all")
    public ResponseEntity<List<Report>> getAllScamWallReports() {
        // Database se saari reports nikal kar frontend ko bhej dega
        return ResponseEntity.ok(reportRepository.findAll());
    }

    @PostMapping("/reports/add")
   public ResponseEntity<Report> addReport(@RequestBody Map<String, Object> payload) {
        
        // Purane fields extract kar rahe hain
        String companyName = (String) payload.get("companyName");
        String websiteUrl = (String) payload.get("websiteUrl");
        if (websiteUrl == null) websiteUrl = (String) payload.get("companyWebsite"); // Fallback
        
        String scamType = (String) payload.get("scamType");
        String description = (String) payload.get("description");
        String userEmail = (String) payload.get("userEmail"); 

        // 🔥 FIX: Naye AI Fields Extract Kar Rahe Hain (Jo pehle hawa mein ud rahe the)
        Integer riskPercentage = null;
        if (payload.get("riskPercentage") != null) {
            riskPercentage = Integer.parseInt(payload.get("riskPercentage").toString());
        }
        
        String verdict = (String) payload.get("verdict");
        String redFlags = (String) payload.get("redFlags");
        String hrEmailDomain = (String) payload.get("hrEmailDomain");
        String paymentDemanded = (String) payload.get("paymentDemanded");
        String interviewTaken = (String) payload.get("interviewTaken");

        // Service ko saara data bhej rahe hain
        Report savedReport = reportService.submitReport(
            companyName, websiteUrl, scamType, description, 
            riskPercentage, verdict, redFlags, hrEmailDomain, paymentDemanded, interviewTaken
        );
        
        // Agar email bheja hai frontend ne, toh usko save karo
        if (userEmail != null && !userEmail.isEmpty()) {
            savedReport.setUserEmail(userEmail);
            reportRepository.save(savedReport);
        }
        return ResponseEntity.ok(savedReport);
    }

    
    @GetMapping("/reports/user/{email}")
    public ResponseEntity<List<Report>> getUserScamWallReports(@PathVariable String email) {
        return ResponseEntity.ok(reportRepository.findByUserEmail(email));
    }

    @DeleteMapping("/reports/{id}")
    public ResponseEntity<String> deleteScamWallReport(@PathVariable Long id) {
        reportRepository.deleteById(id);
        return ResponseEntity.ok("Report deleted successfully");
    }

    // ==========================================
    // 2. SCAN HISTORY APIs
    // ==========================================
    @PostMapping("/scan-history/add")
    public ResponseEntity<ScamReport> saveScanHistory(@RequestBody ScamReport scamReport) {
        ScamReport saved = scamReportRepository.save(scamReport);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/scan-history/user/{email}")
    public ResponseEntity<List<ScamReport>> getUserScanHistory(@PathVariable String email) {
        return ResponseEntity.ok(scamReportRepository.findByUserEmail(email));
    }

    @DeleteMapping("/scan-history/{id}")
    public ResponseEntity<String> deleteScanHistory(@PathVariable Long id) {
        scamReportRepository.deleteById(id);
        return ResponseEntity.ok("Scan history deleted successfully");
    }

    // ==========================================
    // 3. COMPANY & AI APIs
    // ==========================================
    @GetMapping("/companies/search")
    public ResponseEntity<List<Company>> searchCompany(@RequestParam String query) {
        return ResponseEntity.ok(reportService.searchCompanies(query));
    }

    @GetMapping("/companies/{companyId}/reports")
    public ResponseEntity<List<Report>> getReportsByCompany(@PathVariable Long companyId) {
        return ResponseEntity.ok(reportService.getReportsByCompany(companyId));
    }

    @GetMapping("/companies")
    public ResponseEntity<List<Company>> getAllCompanies() {
        return ResponseEntity.ok(reportService.getAllCompanies());
    }

    @PostMapping("/analyze")
    public ResponseEntity<String> analyzeScam(@RequestBody Map<String, String> payload) {
        String companyName = payload.get("companyName");
        String jobDetails = payload.get("jobDetails");
        String aiReport = aiAnalysisService.analyzeOffer(companyName, jobDetails);
        return ResponseEntity.ok(aiReport);
    }

    @PostMapping("/analyze-image")
    public ResponseEntity<String> analyzeOfferImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "companyName", defaultValue = "Unknown") String companyName) { // 👉 Naya parameter add kiya
        
        try {
            byte[] imageBytes = file.getBytes();
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);
            String mimeType = file.getContentType(); 
            
            // 👉 companyName ko aage service mein pass kar do
            String aiReport = aiAnalysisService.analyzeOfferImage(base64Image, mimeType, companyName);
            return ResponseEntity.ok(aiReport);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("{\"error\": \"Image processing failed! " + e.getMessage() + "\"}");
        }
    }
}