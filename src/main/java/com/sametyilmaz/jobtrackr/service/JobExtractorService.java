package com.sametyilmaz.jobtrackr.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class JobExtractorService {

    private static final Logger log = LoggerFactory.getLogger(JobExtractorService.class);

    @Value("${groq.api.key}")
    private String groqApiKey;

    private final WebClient httpClient = WebClient.builder()
            .codecs(c -> c.defaultCodecs()
                    .maxInMemorySize(5 * 1024 * 1024))
            .build();

    private final WebClient groqClient = WebClient.builder()
            .baseUrl("https://api.groq.com")
            .codecs(c -> c.defaultCodecs()
                    .maxInMemorySize(5 * 1024 * 1024))
            .build();

    public Map<String, Object> extractFromUrl(String url) {
        try {
            log.info("Fetching URL: {}", url);

            // Step 1 — Fetch job page
            String html = httpClient.get()
                    .uri(url)
                    .header("User-Agent",
                            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                                    "AppleWebKit/537.36 (KHTML, like Gecko) " +
                                    "Chrome/120.0.0.0 Safari/537.36")
                    .header("Accept", "text/html,application/xhtml+xml")
                    .header("Accept-Language", "en-US,en;q=0.9")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            if (html == null || html.isBlank()) {
                throw new RuntimeException("Could not fetch page content");
            }

            log.info("Fetched HTML length: {}", html.length());

            // Step 2 — Clean HTML to plain text
            String text = html
                    .replaceAll("(?s)<style[^>]*>.*?</style>", " ")
                    .replaceAll("(?s)<script[^>]*>.*?</script>", " ")
                    .replaceAll("<[^>]+>", " ")
                    .replaceAll("&nbsp;", " ")
                    .replaceAll("&amp;", "&")
                    .replaceAll("&lt;", "<")
                    .replaceAll("&gt;", ">")
                    .replaceAll("\\s+", " ")
                    .trim();

            if (text.length() > 8000) {
                text = text.substring(0, 8000);
            }

            log.info("Cleaned text length: {}", text.length());

            // Step 3 — Send to Groq
            String prompt = """
                    Extract job information from this job posting.
                    Return ONLY a valid JSON object, nothing else.
                    No markdown, no backticks, just raw JSON.

                    Required format:
                    {
                      "company": "company name or null",
                      "role": "job title or null",
                      "salaryMin": number or null,
                      "salaryMax": number or null,
                      "location": "location or null",
                      "notes": "2-3 sentence summary of the role and requirements"
                    }

                    Rules:
                    - salary must be annual numbers only (120000 not $120k)
                    - if salary not mentioned use null
                    - return ONLY the JSON object, nothing else

                    Job posting:
                    """ + text;

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "llama-3.1-8b-instant");
            requestBody.put("max_tokens", 500);
            requestBody.put("temperature", 0.1);
            requestBody.put("messages", List.of(
                    Map.of("role", "user", "content", prompt)));

            log.info("Sending to Groq...");

            String response = groqClient.post()
                    .uri("/openai/v1/chat/completions")
                    .header("Authorization", "Bearer " + groqApiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            log.info("Groq response: {}", response);

            // Step 4 — Parse response
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);
            String jsonText = root
                    .path("choices")
                    .get(0)
                    .path("message")
                    .path("content")
                    .asText()
                    .trim();

            log.info("Extracted JSON: {}", jsonText);

            // Strip markdown backticks if model adds them anyway
            if (jsonText.startsWith("```")) {
                jsonText = jsonText
                        .replaceAll("```json", "")
                        .replaceAll("```", "")
                        .trim();
            }

            return mapper.readValue(jsonText,
                    new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {
                    });

        } catch (Exception e) {
            log.error("Extraction failed: {}", e.getMessage(), e);
            throw new RuntimeException(
                    "Failed to extract job data: " + e.getMessage());
        }
    }
}