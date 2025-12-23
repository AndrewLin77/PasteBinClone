package com.pastebin.pastebin_clone.job;

import com.pastebin.pastebin_clone.model.PasteMetadata;
import com.pastebin.pastebin_clone.repository.PasteRepository;
import com.pastebin.pastebin_clone.service.storage.ContentStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PasteCleanupJob {

    private final PasteRepository repository;
    private final ContentStore contentStore;
    private final StringRedisTemplate redisTemplate;

    @Scheduled(fixedRate = 60000) // Every minute
    @Transactional
    public void deleteExpiredPastes() {
        List<PasteMetadata> allPastes = repository.findAll();
        
        for (PasteMetadata paste : allPastes) {
            if (paste.isExpired()) {
                log.info("CLEANUP: Removing paste {}", paste.getShortLink());

                // 1. Delete from S3
                contentStore.deleteContent(paste.getObjectKey());

                // 2. Delete from Redis
                redisTemplate.delete("paste:" + paste.getShortLink());

                // 3. Delete from DB
                repository.delete(paste);
            }
        }
    }
}