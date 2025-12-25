package com.pastebin.pastebin_clone.job;

import com.pastebin.pastebin_clone.model.PasteMetadata;
import com.pastebin.pastebin_clone.repository.PasteMetadataRepository;
import com.pastebin.pastebin_clone.service.DatabaseContentStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class PasteCleanupJob {

    @Autowired
    private PasteMetadataRepository metadataRepository;

    @Autowired
    private DatabaseContentStore contentStore;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Scheduled(fixedRate = 60000) // Runs every minute
    @Transactional
    public void deleteExpiredPastes() {
        List<PasteMetadata> allPastes = metadataRepository.findAll();
        LocalDateTime now = LocalDateTime.now();

        for (PasteMetadata paste : allPastes) {
            // Check if expiration time exists AND is in the past
            if (paste.getExpirationTime() != null && paste.getExpirationTime().isBefore(now)) {
                
                log.info("CLEANUP: Removing expired paste {}", paste.getUrlKey());

                // 1. Delete Blob from "Object Store" (Content Table)
                contentStore.deleteContent(paste.getContentId());

                // 2. Delete from Redis Cache
                redisTemplate.delete(paste.getUrlKey());

                // 3. Delete Metadata from SQL Database
                metadataRepository.delete(paste);
            }
        }
    }
}