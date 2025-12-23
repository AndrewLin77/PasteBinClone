package com.pastebin.pastebin_clone.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "pastes", indexes = {
    @Index(name = "idx_created_at", columnList = "createdAt")
})
@Data
public class PasteMetadata {

    @Id
    @Column(length = 7, nullable = false)
    private String shortLink;

    @Column(nullable = false)
    private String objectKey; // S3 Key

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private Integer expirationMinutes;

    public boolean isExpired() {
        if (expirationMinutes == null || expirationMinutes <= 0) return false;
        return createdAt.plusMinutes(expirationMinutes).isBefore(LocalDateTime.now());
    }
}