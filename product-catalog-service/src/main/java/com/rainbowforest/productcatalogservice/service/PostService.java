package com.rainbowforest.productcatalogservice.service;

import com.rainbowforest.productcatalogservice.entity.Post;
import java.util.List;
import java.util.Optional;

public interface PostService {
    List<Post> getAllPosts();
    Optional<Post> getPostById(Long id);
    Post createPost(Post post);
    Optional<Post> updatePost(Long id, Post post);
    boolean deletePost(Long id);
}
