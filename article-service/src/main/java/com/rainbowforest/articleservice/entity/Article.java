package com.rainbowforest.articleservice.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "articles")
public class Article {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "title", nullable = false, length = 500)
    private String title;
    
    @Column(name = "content", nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String content;
    
    @Column(name = "author_id")
    private Long authorId;
    
    @Column(name = "image_url", columnDefinition = "NVARCHAR(MAX)")
    private String imageUrl;
    
    @Column(name = "category", length = 100)
    private String category;
    
    @Column(name = "tags", columnDefinition = "NVARCHAR(MAX)")
    private String tags; // comma-separated tags
    
    @Column(name = "status", length = 50)
    private String status; // DRAFT, PUBLISHED, ARCHIVED
    
    @Column(name = "view_count")
    private Integer viewCount = 0;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    // Constructors
    public Article() {}
    
    public Article(String title, String content, Long authorId, String imageUrl, String category, String status) {
        this.title = title;
        this.content = content;
        this.authorId = authorId;
        this.imageUrl = imageUrl;
        this.category = category;
        this.status = status;
        this.viewCount = 0;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public Long getAuthorId() {
        return authorId;
    }
    
    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public String getTags() {
        return tags;
    }
    
    public void setTags(String tags) {
        this.tags = tags;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public Integer getViewCount() {
        return viewCount;
    }
    
    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
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
}
