package com.example.urlshortener;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="urls")
public class UrlEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long Id;

    @Column(nullable=false,unique=true)
    private String shortCode;

    @Column(nullable=false)
    private String url;

    @Column(nullable=false,updatable = false)
    private LocalDateTime createdAt=LocalDateTime.now();

    @Column(nullable=false)
    private LocalDateTime updatedAt=LocalDateTime.now();

    @Column(nullable=false)
    private int accessCount=0;

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public String getShortCode() {
        return shortCode;
    }

    public void setShortCode(String shortcode) {
        this.shortCode = shortcode;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public int getAccessCount() {
        return accessCount;
    }

    public void setAccessCount(int accessCount) {
        this.accessCount = accessCount;
    }
}
