package com.pastebin.pastebin_clone.repository;

import com.pastebin.pastebin_clone.model.PasteMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PasteRepository extends JpaRepository<PasteMetadata, String> {
}