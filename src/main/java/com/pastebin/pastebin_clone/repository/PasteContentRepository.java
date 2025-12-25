package com.pastebin.pastebin_clone.repository;

import com.pastebin.pastebin_clone.model.PasteContent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasteContentRepository extends JpaRepository<PasteContent, String> {
}