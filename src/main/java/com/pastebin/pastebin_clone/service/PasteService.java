package com.pastebin.pastebin_clone.service;

import com.pastebin.pastebin_clone.model.PasteMetadata;
import com.pastebin.pastebin_clone.repository.PasteRepository;
import com.pastebin.pastebin_clone.service.storage.ContentStore;
import com.pastebin.pastebin_clone.util.UrlShortener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasteService {

    private final PasteRepository repository;
    private final ContentStore contentStore;
    private final UrlShortener urlShortener;
    private final StringRedisTemplate redisTemplate;

    // WRITE PATH
    public PasteMetadata createPaste(String content, Integer expirationMinutes, String ipAddress) {
        
        // 1. Generate Link
        String shortLink = urlShortener.generateShortLink(ipAddress);
        while (repository.existsById(shortLink)) {
            shortLink = urlShortener.generateShortLink(ipAddress + System.nanoTime());
        }

        // 2. Save Content (S3)
        String objectKey = contentStore.saveContent(content);

        // 3. Save Metadata (SQL)
        PasteMetadata metadata = new PasteMetadata();
        metadata.setShortLink(shortLink);
        metadata.setObjectKey(objectKey);
        metadata.setCreatedAt(LocalDateTime.now());
        metadata.setExpirationMinutes(expirationMinutes);

        PasteMetadata saved = repository.save(metadata);
        
        log.info("WRITE_EVENT: paste_id={}", shortLink);
        return saved;
    }

    // READ PATH
    public String getPasteContent(String shortLink) {
        String cacheKey = "paste:" + shortLink;

        // 1. Check Redis (Cache Hit)
        String cachedContent = redisTemplate.opsForValue().get(cacheKey);
        if (cachedContent != null) {
            log.info("READ_EVENT: paste_id={} source=CACHE", shortLink);
            return cachedContent;
        }

        // 2. Check Database (Cache Miss)
        PasteMetadata metadata = repository.findById(shortLink)
                .orElseThrow(() -> new RuntimeException("Paste not found"));

        if (metadata.isExpired()) {
            throw new RuntimeException("Paste expired");
        }

        // 3. Fetch from S3
        String content = contentStore.getContent(metadata.getObjectKey());

        // 4. Update Redis
        long ttl = 24 * 60; // Default 24 hours
        if (metadata.getExpirationMinutes() != null) {
            long age = Duration.between(metadata.getCreatedAt(), LocalDateTime.now()).toMinutes();
            ttl = metadata.getExpirationMinutes() - age;
        }
        
        if (ttl > 0) {
            redisTemplate.opsForValue().set(cacheKey, content, ttl, TimeUnit.MINUTES);
        }

        log.info("READ_EVENT: paste_id={} source=DB_S3", shortLink);
        return content;
    }
}