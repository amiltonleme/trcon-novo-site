package br.com.trcon.site.chat.controller;

import br.com.trcon.site.chat.dto.ChatRequest;
import br.com.trcon.site.chat.dto.ChatResponse;
import br.com.trcon.site.chat.service.ChatService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/site/chat")
public class ChatController {
    private final ChatService service;
    public ChatController(ChatService service) { this.service = service; }
    @PostMapping
    public ResponseEntity<ChatResponse> chat(@Valid @RequestBody ChatRequest body, HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        String clientKey = forwarded == null || forwarded.isBlank() ? request.getRemoteAddr() : forwarded.split(",")[0].trim();
        return ResponseEntity.ok(service.reply(body, clientKey));
    }
}
