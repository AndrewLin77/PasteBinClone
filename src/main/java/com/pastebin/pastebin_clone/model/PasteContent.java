package com.pastebin.pastebin_clone.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "paste_content")
public class PasteContent {
    @Id
    private String id; // Matches the contentId from metadata

    @Column(nullable = false, columnDefinition = "TEXT")
    private String data; // The actual large text blob
}