package com.example.recyclingapi.service;

import com.example.recyclingapi.model.ItemResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
public class GeminiVisionService {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final String model;

    public GeminiVisionService(
            RestClient.Builder builder,
            ObjectMapper objectMapper,
            @Value("${gemini.api.key}") String apiKey,
            @Value("${gemini.model}") String model) {

        this.restClient = builder
                .baseUrl("https://generativelanguage.googleapis.com")
                .defaultHeader("x-goog-api-key", apiKey)
                .build();

        this.objectMapper = objectMapper;
        this.model = model;
    }

    public ItemResult identify(MultipartFile image) throws IOException {

        String mimeType = image.getContentType();

        if (mimeType == null || !mimeType.startsWith("image/")) {
            mimeType = "image/jpeg";
        }

        String base64Image =
                Base64.getEncoder().encodeToString(image.getBytes());

        String prompt = """
                Identify the main disposable item in this image.

                Classify it using exactly one of these categories:
                Recycle
                Garbage
                Special Disposal

                Return only JSON in this exact format:
                {
                  "name": "specific item name",
                  "category": "Recycle, Garbage, or Special Disposal",
                  "instructions": "short preparation and disposal instructions",
                  "confidence": 0.95
                }

                Recycling rules vary by location, so do not claim that
                acceptance is guaranteed.
                """;

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(
                                Map.of("text", prompt),
                                Map.of("inlineData", Map.of(
                                        "mimeType", mimeType,
                                        "data", base64Image
                                ))
                        ))
                ),
                "generationConfig", Map.of(
                        "responseMimeType", "application/json"
                )
        );

        JsonNode response = restClient.post()
                .uri("/v1beta/models/{model}:generateContent", model)
                .body(requestBody)
                .retrieve()
                .body(JsonNode.class);

        if (response == null) {
            throw new IOException("Gemini returned an empty response.");
        }

        JsonNode textNode = response.at(
                "/candidates/0/content/parts/0/text"
        );

        if (textNode.isMissingNode()) {
            throw new IOException(
                    "Gemini response did not contain an identification."
            );
        }

        String resultText = textNode.asText()
                .replace("```json", "")
                .replace("```", "")
                .trim();

        JsonNode result = objectMapper.readTree(resultText);

        return new ItemResult(
                result.path("name").asText("Unknown Item"),
                result.path("category").asText("Unknown"),
                result.path("instructions")
                        .asText("Check your local disposal requirements."),
                result.path("confidence").asDouble(0.0),
                image.getOriginalFilename()
        );
    }
}