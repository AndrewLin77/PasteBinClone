package com.pastebin.pastebin_clone.util;

import org.springframework.stereotype.Component;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;

@Component
public class UrlShortener {

    private static final String ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int BASE = ALPHABET.length();

    public String generateShortLink(String ipAddress) {
        try {
            String input = ipAddress + "_" + System.currentTimeMillis();
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));
            
            String shortLink = encodeBase62(hash);
            
            if (shortLink.length() > 7) {
                return shortLink.substring(0, 7);
            }
            return shortLink;
        } catch (Exception e) {
            throw new RuntimeException("Error generating hash", e);
        }
    }

    private String encodeBase62(byte[] input) {
        StringBuilder sb = new StringBuilder();
        long num = bytesToLong(input); 

        if (num == 0) return String.valueOf(ALPHABET.charAt(0));

        while (num > 0) {
            int remainder = (int) (num % BASE);
            sb.append(ALPHABET.charAt(remainder));
            num /= BASE;
        }
        return sb.reverse().toString();
    }
    
    private long bytesToLong(byte[] bytes) {
        long result = 0;
        for (int i = 0; i < Math.min(bytes.length, 8); i++) {
            result <<= 8;
            result |= (bytes[i] & 0xFF);
        }
        return Math.abs(result);
    }
}