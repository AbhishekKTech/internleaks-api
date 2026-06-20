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

    // Helper method to execute a single Tavily search query
    private String executeSingleSearch(RestTemplate restTemplate, HttpHeaders headers, String query) {
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("api_key", tavilyApiKey);
            body.put("query", query);
            body.put("search_depth", "basic");
            body.put("max_results", 2); // Top 2 absolute best results per query taaki response fast rahe

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = restTemplate.postForEntity("https://api.tavily.com/search", request, String.class);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(response.getBody());
            JsonNode results = rootNode.path("results");

            StringBuilder sb = new StringBuilder();
            if (results.isArray()) {
                for (JsonNode result : results) {
                    sb.append("- ").append(result.path("content").asText()).append("\n");
                }
            }
            return sb.toString();
        } catch (Exception e) {
            System.err.println("🚨 Single Query Error: " + e.getMessage());
            return "";
        }
    }

    public String searchAdvancedReviews(String companyName, String hrEmailDomain) {
        if (companyName == null || companyName.trim().isEmpty() || companyName.equalsIgnoreCase("Unknown")) {
            System.out.println("⚠️ TAVILY SEARCH SKIPPED: Company name is unknown or empty.");
            return "No valid company name for live web search.";
        }

        System.out.println("🔍 ADVANCED TAVILY SEARCH INITIATED FOR: " + companyName);
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        StringBuilder totalContext = new StringBuilder();

        // 🔍 Query 1: Real user check for general reviews & legitimacy
        String query1 = companyName + " internship reviews authentic or fake";
        System.out.println("🚀 Query 1 Executing: " + query1);
        String res1 = executeSingleSearch(restTemplate, headers, query1);
        if (!res1.isEmpty()) totalContext.append("=== Web Reviews ===\n").append(res1).append("\n");

        // 🔍 Query 2: Direct scam & fraud check on forums
        String query2 = companyName + " scam fraud complaints community discussions";
        System.out.println("🚀 Query 2 Executing: " + query2);
        String res2 = executeSingleSearch(restTemplate, headers, query2);
        if (!res2.isEmpty()) totalContext.append("=== Scam & Complaints ===\n").append(res2).append("\n");

        // 🔍 Query 3: Domain check (if it's not a generic domain like gmail/yahoo)
        if (hrEmailDomain != null && !hrEmailDomain.isEmpty() && !hrEmailDomain.contains("gmail") && !hrEmailDomain.contains("yahoo")) {
            String query3 = hrEmailDomain + " corporate verification identity check";
            System.out.println("🚀 Query 3 Executing: " + query3);
            String res3 = executeSingleSearch(restTemplate, headers, query3);
            if (!res3.isEmpty()) totalContext.append("=== Email Domain Verification ===\n").append(res3).append("\n");
        }

        String finalContext = totalContext.toString();
        if (finalContext.trim().isEmpty()) {
            System.out.println("❌ TAVILY FOUND NO DATA.");
            return "No suspicious reports found on the internet.";
        }
        
        System.out.println("✅ TAVILY FINAL ADVANCED DATA GATHERED:\n" + finalContext);
        return finalContext;
    }
}