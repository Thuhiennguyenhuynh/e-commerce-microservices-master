package com.rainbowforest.articleservice.controller;

import com.rainbowforest.articleservice.entity.Article;
import com.rainbowforest.articleservice.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/articles")
@CrossOrigin(origins = "*")
public class ArticleController {
    
    @Autowired
    private ArticleService articleService;
    
    // Public endpoints
    @GetMapping
    public ResponseEntity<Page<Article>> getAllPublishedArticles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Article> articles = articleService.getAllPublishedArticles(PageRequest.of(page, size));
        return ResponseEntity.ok(articles);
    }
    
    @GetMapping("/latest")
    public ResponseEntity<List<Article>> getLatestArticles() {
        List<Article> articles = articleService.getLatestArticles();
        return ResponseEntity.ok(articles);
    }
    
    @GetMapping("/search")
    public ResponseEntity<Page<Article>> searchArticles(
            @RequestParam String title,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Article> articles = articleService.searchArticles(title, PageRequest.of(page, size));
        return ResponseEntity.ok(articles);
    }
    
    @GetMapping("/category/{category}")
    public ResponseEntity<Page<Article>> getArticlesByCategory(
            @PathVariable String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Article> articles = articleService.getArticlesByCategory(category, PageRequest.of(page, size));
        return ResponseEntity.ok(articles);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Article> getArticleById(@PathVariable Long id) {
        Optional<Article> article = articleService.getArticleById(id);
        return article.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    // Admin endpoints - CRUD operations
    @GetMapping("/admin/all")
    public ResponseEntity<Page<Article>> getAllArticles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Article> articles = articleService.getAllArticles(PageRequest.of(page, size));
        return ResponseEntity.ok(articles);
    }
    
    @PostMapping("/admin")
    public ResponseEntity<Article> createArticle(@RequestBody Article article) {
        Article createdArticle = articleService.createArticle(article);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdArticle);
    }
    
    @PutMapping("/admin/{id}")
    public ResponseEntity<Article> updateArticle(
            @PathVariable Long id,
            @RequestBody Article articleDetails) {
        try {
            Article updatedArticle = articleService.updateArticle(id, articleDetails);
            return ResponseEntity.ok(updatedArticle);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/admin/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable Long id) {
        try {
            articleService.deleteArticle(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
