package com.abhishekktech.internleaks.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
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

    // Fix 1: URL generation with model name
    private String getGeminiUrl(String modelName) {
        return "https://generativelanguage.googleapis.com/v1beta/models/" + modelName + ":generateContent?key=" + geminiApiKey;
    }

    // Fix 2: Timeout settings to avoid hanging connections
    private RestTemplate getRobustRestTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(10000); // 10 seconds to connect
        factory.setReadTimeout(45000);    // 45 seconds to wait for AI response
        return new RestTemplate(factory);
    }

    // Fix 3: Fallback and retry logic
    private ResponseEntity<String> sendRequestWithRetry(HttpEntity<Map<String, Object>> request) {
        RestTemplate restTemplate = getRobustRestTemplate();
        
        String primaryModel = "gemini-flash-latest"; 
        String fallbackModel = "gemini-pro"; 
        
        int retries = 0;
        while (retries < 3) {
            try {
                // Try Flash first, then fallback to Pro
                String currentModel = (retries < 2) ? primaryModel : fallbackModel;
                if (retries == 2) System.out.println("⚠️ Switching to fallback model: " + fallbackModel);
                
                return restTemplate.postForEntity(getGeminiUrl(currentModel), request, String.class);
            } catch (Exception e) {
                System.out.println("🚨 AI Request Failed (Attempt " + (retries + 1) + "): " + e.getMessage());
                if (retries < 2) {
                    try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
                    retries++;
                } else {
                    throw new RuntimeException("All API attempts failed. Last error: " + e.getMessage());
                }
            }
        }
        return null;
    }

    // Fix 4: Return a valid JSON error response
    private String getFallbackErrorJson(String errorMessage) {
        String safeError = errorMessage.replace("\"", "'").replace("\n", " ");
        return "{\n" +
               "  \"riskPercentage\": 50,\n" +
               "  \"redFlags\": [\n" +
               "    \"🚨 API Connection Error: Could not reach Google AI servers.\",\n" +
               "    \"Details: " + safeError + "\",\n" +
               "    \"Please check your backend internet connection or try scanning again later.\"\n" +
               "  ],\n" +
               "  \"verdict\": \"Analysis Failed - Network Error\",\n" +
               "  \"isValidDocument\": true\n" +
               "}";
    }

    // ==========================================
    // 1. TEXT ONLY SCANNER (No Image)
    // ==========================================
    public String analyzeOffer(String companyName, String jobDetails) {
        try {
            String webContext = webSearchService.searchAdvancedReviews(companyName, "");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String promptText = "Act as an expert Cyber-Security Threat Analyst and HR Fraud Investigator. "
                    + "I am providing you with the details of an internship/job offer from '" + companyName + "'.\n"
                    + "User Provided Context: '" + jobDetails + "'.\n\n"
                    + "🌐 **LIVE WEB CONTEXT (from Tavily Internet Search)**:\n"
                    + webContext + "\n\n"
                    + "YOUR TASK:\n"
                    + "1. Calculate a precise Scam Risk Percentage (0-100). Strongly consider the live web context.\n"
                    + "2. Provide 4 to 8 highly detailed 'redFlags'. **CRITICAL INSTRUCTION**: Your VERY FIRST red flag MUST be a summary of what you found in the LIVE WEB CONTEXT. Escape all quotes inside strings.\n"
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
            
            // Use the retry handler
            ResponseEntity<String> response = sendRequestWithRetry(request);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(response.getBody());
            return rootNode.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();

        } catch (Exception e) {
            return getFallbackErrorJson(e.getMessage());
        }
    }

    // ==========================================
    // 2. FULL CONTEXT SCANNER (Image + User Data)
    // ==========================================
    public String analyzeOfferImage(String base64Image, String mimeType, String companyName, 
                                    String companyWebsite, String paymentDemanded, String interviewTaken, 
                                    String hrEmailDomain, String userSuspicionFeedback) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String webContext = webSearchService.searchAdvancedReviews(companyName, hrEmailDomain);

            String finalPrompt = "Act as an expert Cyber-Security Threat Analyst and Advanced HR Fraud Investigator.\n\n"
                    + "Analyze the text inside the uploaded image (an internship/job offer) from '" + companyName + "'.\n\n"
                    + "🎯 **USER-PROVIDED CONTEXT FOR CROSS-VERIFICATION**:\n"
                    + "- Company Website: " + companyWebsite + "\n"
                    + "- User Claims Payment Was Demanded: " + paymentDemanded + "\n"
                    + "- User Claims Proper Interview Was Taken: " + interviewTaken + "\n"
                    + "- Recruiter HR Email Domain: " + hrEmailDomain + "\n"
                    + "- Why the User is Suspicious: \"" + userSuspicionFeedback + "\"\n\n"
                    + "🌐 **LIVE DEEP WEB CONTEXT (Real-time search results)**:\n"
                    + webContext + "\n\n"
                    + "CRITICAL INSTRUCTIONS FOR CROSS-VERIFICATION:\n"
                    + "1. Validate the user's feedback. If the user noted suspicion, cross-reference it with the document text and web context.\n"
                    + "2. Verify the Email Domain. If the domain is free (gmail/yahoo) but claims to be official, flag as a major anomaly.\n"
                    + "3. Synthesize everything into a final calculated Scam Risk Percentage (0-100).\n\n"
                    + "YOUR TASK:\n"
                    + "- Return 4 to 8 highly actionable 'redFlags'. Ensure the first flag summarizes the Web Context.\n"
                    + "- Select exactly one 'verdict': 'Confirmed Fraudulent Offer', 'High Probability Scam', 'Likely Fake Offer', 'Low Risk - Appears Legitimate', or 'Invalid Document'.\n\n"
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
            
            // Use the retry handler
            ResponseEntity<String> response = sendRequestWithRetry(request);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(response.getBody());
            return rootNode.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();

        } catch (Exception e) {
            return getFallbackErrorJson(e.getMessage());
        }
    }
}