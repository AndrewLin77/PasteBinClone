package com.pastebin.pastebin_clone.service.storage;

public interface ContentStore {
    String saveContent(String content);
    String getContent(String objectKey);
    void deleteContent(String objectKey);
}