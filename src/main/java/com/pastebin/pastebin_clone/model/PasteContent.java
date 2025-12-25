package com.pastebin.pastebin_clone.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "paste_contents")
@Data
@NoArgsConstructor
public class PasteContent {

    @Id
    private String objectKey; // Matches the key in PasteMetadata

    @Lob // Large Object (tells DB this is a big text blob)
    @Column(nullable = false)
    private String data;

    public PasteContent(String objectKey, String data) {
        this.objectKey = objectKey;
        this.data = data;
    }
}