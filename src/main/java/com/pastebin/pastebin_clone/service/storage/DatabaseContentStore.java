package com.pastebin.pastebin_clone.service.storage;

import com.pastebin.pastebin_clone.model.PasteContent;
import com.pastebin.pastebin_clone.repository.PasteContentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
@Primary // Tells Spring: "Use this one, ignore the FileSystem one"
@RequiredArgsConstructor
public class DatabaseContentStore implements ContentStore {

    private final PasteContentRepository repository;

    @Override
    public String saveContent(String content) {
        String objectKey = UUID.randomUUID().toString();
        PasteContent pasteContent = new PasteContent(objectKey, content);
        repository.save(pasteContent);
        return objectKey;
    }

    @Override
    public String getContent(String objectKey) {
        return repository.findById(objectKey)
                .map(PasteContent::getData)
                .orElse(null);
    }

    @Override
    public void deleteContent(String objectKey) {
        repository.deleteById(objectKey);
    }
}