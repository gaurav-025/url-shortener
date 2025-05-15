package com.example.urlshortener;

//import jakarta.persistence.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection="urls")
@CompoundIndex(name="shortCode_index", def = "{'shortCode': 1}", unique = true)
public class UrlEntity {
    @Id
    private String Id;

    private String shortCode;

    private String url;

    private LocalDateTime createdAt=LocalDateTime.now();

    private LocalDateTime updatedAt=LocalDateTime.now();

    private int accessCount=0;

    public String getId() {
        return Id;
    }

    public void setId(String id) {
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
