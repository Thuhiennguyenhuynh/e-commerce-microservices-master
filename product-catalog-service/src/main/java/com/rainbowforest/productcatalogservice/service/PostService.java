package com.rainbowforest.productcatalogservice.service;

import com.rainbowforest.productcatalogservice.entity.Post;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface PostService {
    List<Post> getAllPosts();
    Optional<Post> getPostById(Long id);
    Post createPost(Post post, MultipartFile imageFile) throws IOException;
    Optional<Post> updatePost(Long id, Post post, MultipartFile imageFile) throws IOException;
    boolean deletePost(Long id);
}