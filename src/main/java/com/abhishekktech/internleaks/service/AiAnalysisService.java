package com.abhishekktech.internleaks.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.util.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;

@Service
public class AiAnalysisService {

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    private String getGeminiUrl() {
        return "https://generativelanguage.googleapis.com/v1beta/models/gemini-flash-latest:generateContent?key=" + geminiApiKey;
    }

    public String analyzeOffer(String companyName, String jobDetails) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String promptText = "Act as an expert HR and Fraud Investigator. Analyze this internship offer from '" 
                    + companyName + "'. Details: '" + jobDetails + "'. "
                    + "First, determine if this input actually describes a job/internship offer. If it is a resume, random text, or completely unrelated, set 'isValidDocument' to false. "
                    + "Calculate the probability of this being a scam (Risk Percentage 0-100). "
                    + "Provide 3 key red flags or positive signs. "
                    + "For 'verdict', strictly choose exactly ONE of these options: 'Confirmed Fraudulent Offer', 'High Probability Scam', 'Likely Fake Offer', 'Low Risk - Appears Legitimate', or 'Invalid Document'. "
                    + "Return ONLY a valid JSON with keys: 'riskPercentage' (integer), 'redFlags' (array of strings), 'verdict' (string), 'isValidDocument' (boolean).";

            Map<String, Object> part = new HashMap<>();
            part.put("text", promptText);
            
            Map<String, Object> content = new HashMap<>();
            content.put("parts", Collections.singletonList(part));
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("contents", Collections.singletonList(content));

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(getGeminiUrl(), request, String.class);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(response.getBody());
            
            String cleanAiReport = rootNode.path("candidates").get(0)
                                           .path("content").path("parts").get(0)
                                           .path("text").asText();
            
            cleanAiReport = cleanAiReport.replace("```json\n", "").replace("\n```", "").trim();
            return cleanAiReport; 

        } catch (Exception e) {
            return "{\"error\": \"AI Analysis Engine Down! " + e.getMessage() + "\"}";
        }
    }

    public String analyzeOfferImage(String base64Image, String mimeType) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String promptText = "Act as an expert HR and Fraud Investigator. Analyze the text within this image. "
                    + "First, determine if this image is actually an internship/job offer letter or HR email. If it is a resume, selfie, landscape, or unrelated document, set 'isValidDocument' to false. "
                    + "Calculate the probability of this being a scam (Risk Percentage 0-100). "
                    + "Provide 3 key red flags or positive signs based ONLY on the image text. "
                    + "For 'verdict', strictly choose exactly ONE of these options: 'Confirmed Fraudulent Offer', 'High Probability Scam', 'Likely Fake Offer', 'Low Risk - Appears Legitimate', or 'Invalid Document'. "
                    + "Return ONLY a valid JSON with keys: 'riskPercentage' (integer), 'redFlags' (array of strings), 'verdict' (string), 'isValidDocument' (boolean).";

            Map<String, Object> textPart = new HashMap<>();
            textPart.put("text", promptText);

            Map<String, Object> inlineData = new HashMap<>();
            inlineData.put("mimeType", mimeType);
            inlineData.put("data", base64Image);

            Map<String, Object> imagePart = new HashMap<>();
            imagePart.put("inlineData", inlineData);

            List<Map<String, Object>> partsList = new ArrayList<>();
            partsList.add(textPart);
            partsList.add(imagePart);

            Map<String, Object> content = new HashMap<>();
            content.put("parts", partsList);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("contents", Collections.singletonList(content));

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(getGeminiUrl(), request, String.class);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(response.getBody());

            String cleanAiReport = rootNode.path("candidates").get(0)
                    .path("content").path("parts").get(0)
                    .path("text").asText();

            cleanAiReport = cleanAiReport.replace("```json\n", "").replace("\n```", "").trim();
            return cleanAiReport;

        } catch (Exception e) {
            return "{\"error\": \"Image Analysis Engine Down! " + e.getMessage() + "\"}";
        }
    }
}