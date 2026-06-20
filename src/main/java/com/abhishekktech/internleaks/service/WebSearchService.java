package com.abhishekktech.internleaks.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

@Service
public class WebSearchService {

    @Value("${tavily.api.key}")
    private String tavilyApiKey;

    public String searchCompanyReviews(String companyName) {
        if (companyName == null || companyName.trim().isEmpty() || companyName.equalsIgnoreCase("Unknown")) {
            System.out.println("⚠️ TAVILY SEARCH SKIPPED: Company name is unknown or empty.");
            return "No company name provided for web search.";
        }
        
        try {
            System.out.println("🔍 TAVILY SEARCH INITIATED FOR: " + companyName);
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // 👉 FIX: Strict Reddit/Quora hata diya. Ab poore internet pe scam reviews dhoondhega.
            String query = companyName + " internship fake scam fraud reviews";

            Map<String, Object> body = new HashMap<>();
            body.put("api_key", tavilyApiKey);
            body.put("query", query);
            body.put("search_depth", "basic");
            body.put("max_results", 3);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = restTemplate.postForEntity("https://api.tavily.com/search", request, String.class);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(response.getBody());
            JsonNode results = rootNode.path("results");

            StringBuilder contextBuilder = new StringBuilder();
            if (results.isArray() && results.size() > 0) {
                for (JsonNode result : results) {
                    contextBuilder.append("- ").append(result.path("content").asText()).append("\n");
                }
                String finalResult = contextBuilder.toString();
                System.out.println("✅ TAVILY FOUND DATA:\n" + finalResult);
                return finalResult;
            } else {
                System.out.println("❌ TAVILY FOUND NO DATA FOR THIS COMPANY.");
                return "No suspicious reports found on the internet.";
            }
        } catch (Exception e) {
            System.err.println("🚨 TAVILY ERROR: " + e.getMessage());
            return "Could not fetch web context due to an error.";
        }
    }
}