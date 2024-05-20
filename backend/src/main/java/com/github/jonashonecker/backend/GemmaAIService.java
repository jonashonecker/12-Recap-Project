package com.github.jonashonecker.backend;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jonashonecker.backend.ollamaGemma2b.GemmaRequest;
import com.github.jonashonecker.backend.ollamaGemma2b.GemmaResponse;
import com.github.jonashonecker.backend.ollamaGemma2b.ResponsePropertyJson;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class GemmaAIService {
    private final RestClient restClient;

    public GemmaAIService(@Value("${gemma_url}") String gemmaUrl) {
        this.restClient = RestClient.create(gemmaUrl);
    }

    public String improveTextQuality(String text) {
        ObjectMapper objectMapper = new ObjectMapper();
        String prompt = "Correct the following text for grammar and spelling. Provide the corrected text in a JSON containing a property called corrected_text: " + text;
        GemmaResponse gemmaResponse = this.restClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .body(new GemmaRequest(prompt))
                .retrieve()
                .body(GemmaResponse.class);
        assert gemmaResponse != null;
        try {
            return objectMapper.readValue(gemmaResponse.response(), ResponsePropertyJson.class).corrected_text();
        } catch (JsonProcessingException jsonProcessingException1) {
            try {
                return objectMapper.readValue(gemmaResponse.response(), ResponsePropertyJson.class).corrected_text();
            } catch (JsonProcessingException jsonProcessingException2) {
                return text;
            }
        }
    }
}
