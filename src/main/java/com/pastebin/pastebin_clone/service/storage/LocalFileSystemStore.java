package com.pastebin.pastebin_clone.service.storage;

import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LocalFileSystemStore implements ContentStore {

    // Simulates S3
    private final Map<String, String> mockS3Bucket = new ConcurrentHashMap<>();

    @Override
    public String saveContent(String content) {
        String objectKey = UUID.randomUUID().toString();
        mockS3Bucket.put(objectKey, content);
        return objectKey;
    }

    @Override
    public String getContent(String objectKey) {
        return mockS3Bucket.get(objectKey);
    }
    
    @Override
    public void deleteContent(String objectKey) {
        mockS3Bucket.remove(objectKey);
    }
}