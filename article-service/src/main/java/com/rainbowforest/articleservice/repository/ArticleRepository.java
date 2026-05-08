package com.rainbowforest.articleservice.repository;

import com.rainbowforest.articleservice.entity.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    
    Page<Article> findByStatus(String status, Pageable pageable);
    
    Page<Article> findByStatusAndCategory(String status, String category, Pageable pageable);
    
    Page<Article> findByStatusAndTitleContainingIgnoreCase(String status, String title, Pageable pageable);
    
    Page<Article> findByStatusAndCategoryAndTitleContainingIgnoreCase(String status, String category, String title, Pageable pageable);
    
    @Query(value = "SELECT TOP 6 * FROM articles WHERE status = 'PUBLISHED' ORDER BY created_at DESC", nativeQuery = true)
    List<Article> findLatestPublished();
    
    List<Article> findByStatusOrderByCreatedAtDesc(String status);
}
