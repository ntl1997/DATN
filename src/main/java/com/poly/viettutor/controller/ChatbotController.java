package com.poly.viettutor.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chatbot")
public class ChatbotController {

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @Value("${gemini.model:gemini-1.5-flash}")
    private String geminiModel;

    @Value("${gemini.endpoint:https://generativelanguage.googleapis.com/v1beta/models}")
    private String geminiEndpoint;

    private final RestTemplate rest = new RestTemplate();

    @PostMapping
    public ResponseEntity<?> chatWithBot(@RequestBody Map<String, String> payload) {
        String userMessage = payload.get("message");
        if (userMessage == null || userMessage.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("reply", "Câu hỏi không hợp lệ."));
        }

        // Payload đúng chuẩn Gemini: generateContent
        Map<String, Object> body = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(
                                Map.of("text", userMessage)))));

        // URL đúng của Gemini
        String url = String.format("%s/%s:generateContent?key=%s",
                geminiEndpoint, geminiModel, geminiApiKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            Map<?, ?> res = rest.postForObject(url, entity, Map.class);
            // Parse: candidates[0].content.parts[0].text
            if (res == null)
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Gemini trả về rỗng");

            List<?> candidates = (List<?>) res.get("candidates");
            if (candidates == null || candidates.isEmpty())
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Gemini: không có candidates");

            Map<?, ?> cand0 = (Map<?, ?>) candidates.get(0);
            Map<?, ?> content = (Map<?, ?>) cand0.get("content");
            List<?> parts = (List<?>) content.get("parts");
            if (parts == null || parts.isEmpty())
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Gemini: không có parts");

            String text = (String) ((Map<?, ?>) parts.get(0)).get("text");
            if (text == null || text.isBlank())
                text = "Xin lỗi, tôi không có câu trả lời.";

            return ResponseEntity.ok(Map.of("reply", text));

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            // Log ngắn gọn; trong dự án thực tế hãy log stacktrace
            System.out.println("Lỗi gọi Gemini: " + e.getMessage());
            return ResponseEntity.status(502).body(Map.of(
                    "reply", "Không gọi được Gemini (kiểm tra endpoint/model/key)."));
        }
    }
}
