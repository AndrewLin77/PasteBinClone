package com.pastebin.pastebin_clone.service;

import org.springframework.stereotype.Service;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.UUID;

@Service
public class UrlShortenerService {
    
    private static final String BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public String generateShortKey() {
        String input = Instant.now().toString() + UUID.randomUUID().toString();
        String hash = md5(input);
        
        // Base62 Encode the hash and take first 7 chars
        return base62Encode(hash).substring(0, 7);
    }

    private String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : messageDigest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error generating MD5", e);
        }
    }

    private String base62Encode(String hex) {
        // Simple Base62 implementation logic
        // (For production, use a library, but this works for demo)
        StringBuilder sb = new StringBuilder();
        for (char c : hex.toCharArray()) {
            sb.append(BASE62.charAt(c % 62));
        }
        return sb.toString();
    }
}