package com.github.jonashonecker.backend;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class GemmaAIServiceTest {

    private static MockWebServer mockWebServer;
    private static GemmaAIService gemmaAIService;

    @BeforeAll
    static void beforeAll() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        gemmaAIService = new GemmaAIService(mockWebServer.url("/").toString());
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void improveTextQuality_whenInvalidAnswerFromApi_returnOriginalText() {
        //GIVEN
        mockWebServer.enqueue(new MockResponse()
                .setBody("""
                        {
                        "response": "\\"corrected_text\\": \\"abc\\""
                        }
                        """)
                .addHeader("Content-Type", "application/json"));

        mockWebServer.enqueue(new MockResponse()
                .setBody("""
                        {
                        "response": "{\\"sabotage\\": \\"def\\"}"
                        }
                        """)
                .addHeader("Content-Type", "application/json"));

        String text = "Das ist ein Test";

        //WHEN
        String actual = gemmaAIService.improveTextQuality(text);

        //THEN
        assertEquals(text, actual);
    }

    @Test
    void improveTextQuality_whenFirstInvalidThenValidAnswer_returnDescriptionFromValidAnswer() {
        //GIVEN
        mockWebServer.enqueue(new MockResponse()
                .setBody("""
                        {
                        "response": "\\"correct_answer\\": \\"abc\\""
                        }
                        """)
                .addHeader("Content-Type", "application/json"));

        mockWebServer.enqueue(new MockResponse()
                .setBody("""
                        {
                        "response": "{\\"corrected_text\\": \\"def\\"}"
                        }
                        """)
                .addHeader("Content-Type", "application/json"));

        String text = "def";

        //WHEN
        String actual = gemmaAIService.improveTextQuality(text);

        //THEN
        assertEquals(text, actual);
    }
}