package com.github.jonashonecker.backend.ollamaGemma2b;

public record GemmaRequest(
        String model,
        String prompt,
        boolean stream,
        String format
) {
    public GemmaRequest(String prompt) {
        this("gemma:2b", prompt, false, "json");
    }
}
