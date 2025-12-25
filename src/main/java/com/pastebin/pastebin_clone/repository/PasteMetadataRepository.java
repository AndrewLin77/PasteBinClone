package com.pastebin.pastebin_clone.repository;

import com.pastebin.pastebin_clone.model.PasteMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PasteMetadataRepository extends JpaRepository<PasteMetadata, Long> {
    Optional<PasteMetadata> findByUrlKey(String urlKey);
}