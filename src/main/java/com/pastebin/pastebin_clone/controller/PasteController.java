package com.pastebin.pastebin_clone.controller;

import com.pastebin.pastebin_clone.model.PasteMetadata;
import com.pastebin.pastebin_clone.service.PasteService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/paste")
@RequiredArgsConstructor
public class PasteController {

    private final PasteService pasteService;

    public record PasteRequest(String content, Integer expirationMinutes) {}
    public record PasteResponse(String shortLink, String url) {}

    @PostMapping
    public ResponseEntity<PasteResponse> createPaste(@RequestBody PasteRequest request, HttpServletRequest servletRequest) {
        String ip = servletRequest.getRemoteAddr();
        PasteMetadata metadata = pasteService.createPaste(request.content(), request.expirationMinutes(), ip);
        String fullUrl = "http://localhost:8080/api/v1/paste/" + metadata.getShortLink();
        return ResponseEntity.ok(new PasteResponse(metadata.getShortLink(), fullUrl));
    }

    @GetMapping("/{shortLink}")
    public ResponseEntity<String> getPaste(@PathVariable String shortLink) {
        try {
            String content = pasteService.getPasteContent(shortLink);
            return ResponseEntity.ok(content);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}