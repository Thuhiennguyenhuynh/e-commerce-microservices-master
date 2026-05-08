package com.rainbowforest.articleservice.service;

import com.rainbowforest.articleservice.entity.Article;
import com.rainbowforest.articleservice.repository.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ArticleService {
    
    @Autowired
    private ArticleRepository articleRepository;
    
    // Public endpoints - anyone can read
    public Page<Article> getAllPublishedArticles(Pageable pageable) {
        return articleRepository.findByStatus("PUBLISHED", pageable);
    }
    
    public Page<Article> searchArticles(String title, Pageable pageable) {
        return articleRepository.findByStatusAndTitleContainingIgnoreCase("PUBLISHED", title, pageable);
    }
    
    public Page<Article> getArticlesByCategory(String category, Pageable pageable) {
        return articleRepository.findByStatusAndCategory("PUBLISHED", category, pageable);
    }
    
    public List<Article> getLatestArticles() {
        return articleRepository.findLatestPublished();
    }
    
    public Optional<Article> getArticleById(Long id) {
        Optional<Article> article = articleRepository.findById(id);
        
        // Increment view count
        article.ifPresent(a -> {
            a.setViewCount(a.getViewCount() + 1);
            articleRepository.save(a);
        });
        
        return article;
    }
    
    // Admin endpoints
    public Article createArticle(Article article) {
        article.setCreatedAt(LocalDateTime.now());
        article.setUpdatedAt(LocalDateTime.now());
        article.setViewCount(0);
        return articleRepository.save(article);
    }
    
    public Article updateArticle(Long id, Article articleDetails) {
        Article article = articleRepository.findById(id).orElseThrow();
        
        article.setTitle(articleDetails.getTitle());
        article.setContent(articleDetails.getContent());
        article.setImageUrl(articleDetails.getImageUrl());
        article.setCategory(articleDetails.getCategory());
        article.setTags(articleDetails.getTags());
        article.setStatus(articleDetails.getStatus());
        article.setUpdatedAt(LocalDateTime.now());
        
        return articleRepository.save(article);
    }
    
    public void deleteArticle(Long id) {
        articleRepository.deleteById(id);
    }
    
    public Page<Article> getAllArticles(Pageable pageable) {
        return articleRepository.findAll(pageable);
    }
}
