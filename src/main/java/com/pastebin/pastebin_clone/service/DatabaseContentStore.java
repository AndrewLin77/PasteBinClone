package com.pastebin.pastebin_clone.service;

import com.pastebin.pastebin_clone.model.PasteContent;
import com.pastebin.pastebin_clone.repository.PasteContentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class DatabaseContentStore {

    @Autowired
    private PasteContentRepository contentRepository;

    public String saveContent(String textData) {
        String contentId = UUID.randomUUID().toString();
        PasteContent content = new PasteContent();
        content.setId(contentId);
        content.setData(textData);
        contentRepository.save(content);
        return contentId;
    }

    public String getContent(String contentId) {
        return contentRepository.findById(contentId)
                .map(PasteContent::getData)
                .orElseThrow(() -> new RuntimeException("Content Blob not found"));
    }

    // --- NEW METHOD ADDED FOR CLEANUP JOB ---
    public void deleteContent(String contentId) {
        if(contentId != null) {
            contentRepository.deleteById(contentId);
        }
    }
}