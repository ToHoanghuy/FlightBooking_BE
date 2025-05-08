package com.example.FlightBooking.Controller.ChatGPT;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin
@Tag(name = "Chat GPT")
public class ChatController {

    @Value("${openai.api-key}")
    private String openaiApiKey;

    @Autowired(required = true)
    RestTemplate restTemplate;

//    @GetMapping("/query")
//    public ResponseEntity<String> query(@RequestParam String query) {
//        if (query == null || query.isEmpty()) {
//            return new ResponseEntity<>("Query cannot be empty", HttpStatus.BAD_REQUEST);
//        }
//
//        String chatGptResponse = getChatGptResponse(query);
//
//        if (chatGptResponse == null) {
//            return new ResponseEntity<>("Failed to get response from ChatGPT", HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//
//        return ResponseEntity.ok(chatGptResponse);
//    }

//    private String getChatGptResponse(String prompt) {
//        String url = "https://api.openai.com/v1/chat/completions";
//        Map<String, Object> requestBody = new HashMap<>();
//        requestBody.put("model", "gpt-3.5-turbo"); // Updated model
//        requestBody.put("prompt", prompt);
//        requestBody.put("max_tokens", 150);
//        requestBody.put("temperature", 0.7); // Optional: Add temperature parameter for response variability
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("Authorization", "Bearer " + openaiApiKey);
//        headers.set("Content-Type", "application/json");
//
//        ResponseEntity<Map> response = restTemplate.postForEntity(url, new org.springframework.http.HttpEntity<>(requestBody, headers), Map.class);
//
//        if (response.getStatusCode() == HttpStatus.OK) {
//            Map<String, Object> responseBody = response.getBody();
//            if (responseBody != null && responseBody.containsKey("choices")) {
//                List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
//                if (!choices.isEmpty()) {
//                    return (String) choices.get(0).get("text");
//                }
//            }
//        } else {
//            System.out.println("Error from OpenAI API: " + response.getStatusCode() + " - " + response.getBody());
//        }
//        return null;
//    }
    @PostMapping("/hitopenaiapi")
    public String getOpenaiResponse(@RequestParam String prompt)
    {
        ChatCompletionRequest chatCompletionRequest = new ChatCompletionRequest("gpt-3.5-turbo", prompt);
        ChatCompletionResponse response= restTemplate.postForObject("https://api.openai.com/v1/chat/completions",chatCompletionRequest,ChatCompletionResponse.class);
        return response.getChoices().get(0).getMessage().getContent();
    }
}
