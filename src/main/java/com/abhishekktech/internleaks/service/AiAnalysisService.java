package com.abhishekktech.internleaks.service;

import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private WebSearchService webSearchService;

    private String getGeminiUrl() {
        return "https://generativelanguage.googleapis.com/v1beta/models/gemini-flash-latest:generateContent?key=" + geminiApiKey;
    }

    public String analyzeOffer(String companyName, String jobDetails) {
        try {
            // 1. Tavily se real-time web search maro
            String webContext = webSearchService.searchCompanyReviews(companyName);

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // 2. NAYA PROMPT (Document Data + Web Search Data)
            String promptText = "Act as an expert Cyber-Security Threat Analyst and HR Fraud Investigator. "
                    + "I am providing you with the details of an internship/job offer from '" + companyName + "'. Details: '" + jobDetails + "'.\n\n"
                    + "🌐 **LIVE WEB CONTEXT (from Reddit/Quora)**:\n"
                    + webContext + "\n\n"
                    + "First, determine if this input actually describes a job/internship offer. If it is random text or unrelated, set 'isValidDocument' to false.\n\n"
                    + "YOUR TASK:\n"
                    + "1. Calculate a precise Scam Risk Percentage (0-100). Strongly consider the live web context. If people are calling it a scam online, the risk should be very high (80%+).\n"
                    + "2. Provide 4 to 8 highly detailed 'redFlags' (or green flags). Use both the document details AND the web context to explain exactly why it's suspicious. Mention community reviews if applicable. Escape all quotes inside strings.\n"
                    + "3. For 'verdict', strictly choose ONE: 'Confirmed Fraudulent Offer', 'High Probability Scam', 'Likely Fake Offer', 'Low Risk - Appears Legitimate', or 'Invalid Document'.\n\n"
                    + "Return ONLY a valid JSON with keys: 'riskPercentage' (integer), 'redFlags' (array of strings), 'verdict' (string), 'isValidDocument' (boolean).";

            Map<String, Object> part = new HashMap<>();
            part.put("text", promptText);
            
            Map<String, Object> content = new HashMap<>();
            content.put("parts", Collections.singletonList(part));
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("contents", Collections.singletonList(content));
            
            Map<String, Object> generationConfig = new HashMap<>();
            generationConfig.put("responseMimeType", "application/json");
            requestBody.put("generationConfig", generationConfig);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(getGeminiUrl(), request, String.class);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(response.getBody());
            
            return rootNode.path("candidates").get(0)
                           .path("content").path("parts").get(0)
                           .path("text").asText();

        } catch (Exception e) {
            return "{\"error\": \"AI Analysis Engine Down! " + e.getMessage() + "\"}";
        }
    }

    // 👉 Parameter mein companyName receive kiya
    // 👉 FIX: Yahan 3rd parameter 'companyName' add kiya hai
    public String analyzeOfferImage(String base64Image, String mimeType, String companyName) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // 1. Direct Web Search from Form Data
            String webContext = "No web context found.";
            if (companyName != null && !companyName.trim().isEmpty() && !companyName.equalsIgnoreCase("Unknown")) {
                webContext = webSearchService.searchCompanyReviews(companyName);
            }

            // 2. Final Analysis with Image + Web Context
            // ==========================================
            // STEP 2: Final Analysis with Image + Web Context
            // ==========================================
            String finalPrompt = "Act as an expert Cyber-Security Threat Analyst and HR Fraud Investigator. "
                    + "Analyze the text within this uploaded image, which claims to be a job/internship offer from '" + companyName + "'.\n\n"
                    + "🌐 **LIVE WEB CONTEXT (from Tavily Internet Search)**:\n"
                    + webContext + "\n\n"
                    + "First, determine if this image is actually an offer letter or HR email. If it is a selfie, landscape, or unrelated, set 'isValidDocument' to false.\n\n"
                    + "YOUR TASK:\n"
                    + "1. Calculate a precise Scam Risk Percentage (0-100). Strongly consider the live web context.\n"
                    + "2. Provide 4 to 8 highly detailed 'redFlags'. **CRITICAL INSTRUCTION**: Your VERY FIRST red flag MUST be a summary of what you found in the LIVE WEB CONTEXT. Even if the web context says 'No suspicious reports found', you must state: 'Web Reputation Check: No negative reports found online' or 'Web Reputation Check: Found online reports stating...'.\n"
                    + "3. Explain EXACTLY why other things are suspicious based on the image text. Escape all quotes inside strings.\n"
                    + "4. For 'verdict', strictly choose ONE: 'Confirmed Fraudulent Offer', 'High Probability Scam', 'Likely Fake Offer', 'Low Risk - Appears Legitimate', or 'Invalid Document'.\n\n"
                    + "Return ONLY a valid JSON with keys: 'riskPercentage' (integer), 'redFlags' (array of strings), 'verdict' (string), 'isValidDocument' (boolean).";

            Map<String, Object> textPart = new HashMap<>();
            textPart.put("text", finalPrompt);

            Map<String, Object> inlineData = new HashMap<>();
            inlineData.put("mimeType", mimeType);
            inlineData.put("data", base64Image);

            Map<String, Object> imagePart = new HashMap<>();
            imagePart.put("inlineData", inlineData);

            Map<String, Object> content = new HashMap<>();
            content.put("parts", Arrays.asList(textPart, imagePart));

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("contents", Collections.singletonList(content));
            
            Map<String, Object> generationConfig = new HashMap<>();
            generationConfig.put("responseMimeType", "application/json");
            requestBody.put("generationConfig", generationConfig);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(getGeminiUrl(), request, String.class);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(response.getBody());

            return rootNode.path("candidates").get(0)
                           .path("content").path("parts").get(0)
                           .path("text").asText();

        } catch (Exception e) {
            return "{\"error\": \"Image Analysis Engine Down! " + e.getMessage() + "\"}";
        }
    }
}