package com.pastebin.pastebin_clone.service;

import com.pastebin.pastebin_clone.dto.PasteRequest;
import com.pastebin.pastebin_clone.dto.PasteResponse;
import com.pastebin.pastebin_clone.model.PasteMetadata;
import com.pastebin.pastebin_clone.repository.PasteMetadataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
public class PasteService {

    @Autowired
    private PasteMetadataRepository metadataRepository;

    @Autowired
    private DatabaseContentStore contentStore;

    @Autowired
    private UrlShortenerService urlShortener;

    @Autowired
    private StringRedisTemplate redisTemplate;

    // This reads the URL from application.properties
    // If you are on localhost, it uses localhost.
    // If you are in production, you can change this without changing code!
    @Value("${app.base-url:http://localhost:8080/api/v1/paste/}")
    private String baseUrl;

    /**
     * WRITE PATH: Creates a new paste
     * 1. Generates Short Key
     * 2. Saves heavy content to Blob Store (Content Table)
     * 3. Saves metadata (with expiration) to SQL
     * 4. Returns the full URL
     */
    @Transactional
    public PasteResponse createPaste(PasteRequest request) {
        String shortKey = urlShortener.generateShortKey();

        // 1. Save the Content (The heavy text)
        String contentId = contentStore.saveContent(request.getContent());

        // 2. Create Metadata (The details)
        PasteMetadata metadata = new PasteMetadata();
        metadata.setUrlKey(shortKey);
        metadata.setCreatedAt(LocalDateTime.now());
        metadata.setContentId(contentId);

        // 3. Handle Expiration (if provided in JSON)
        if (request.getExpirationMinutes() > 0) {
            metadata.setExpirationTime(LocalDateTime.now().plusMinutes(request.getExpirationMinutes()));
        }

        metadataRepository.save(metadata);

        // 4. Construct Response
        PasteResponse response = new PasteResponse();
        response.setKey(shortKey);
        response.setUrl(baseUrl + shortKey);

        return response;
    }

    /**
     * READ PATH: Retrieves a paste
     * 1. Checks Redis Cache first (Fastest)
     * 2. Checks SQL Database for Metadata
     * 3. Checks if expired
     * 4. Fetches content from Blob Store
     * 5. Refills Cache
     */
    public String getPaste(String urlKey) {
        // 1. Cache Hit?
        String cachedContent = redisTemplate.opsForValue().get(urlKey);
        if (cachedContent != null) {
            return cachedContent;
        }

        // 2. Database Lookup
        PasteMetadata metadata = metadataRepository.findByUrlKey(urlKey)
                .orElseThrow(() -> new RuntimeException("Paste not found"));

        // 3. Expiration Check
        if (metadata.getExpirationTime() != null && metadata.getExpirationTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("This paste has expired.");
        }

        // 4. Content Fetch
        String content = contentStore.getContent(metadata.getContentId());

        // 5. Cache Miss -> Set Cache (Expire cache in 10 minutes to save RAM)
        redisTemplate.opsForValue().set(urlKey, content, 10, TimeUnit.MINUTES);

        return content;
    }
}