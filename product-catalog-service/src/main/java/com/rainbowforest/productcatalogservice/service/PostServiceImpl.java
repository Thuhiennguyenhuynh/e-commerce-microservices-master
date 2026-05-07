package com.rainbowforest.productcatalogservice.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.rainbowforest.productcatalogservice.entity.Post;
import com.rainbowforest.productcatalogservice.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private Cloudinary cloudinary;

    @Override
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    @Override
    public Optional<Post> getPostById(Long id) {
        return postRepository.findById(id);
    }

    @Override
    public Post createPost(Post post, MultipartFile imageFile) throws IOException {
        if (imageFile != null && !imageFile.isEmpty()) {
            var uploaded = cloudinary.uploader().upload(imageFile.getBytes(), ObjectUtils.asMap("folder", "posts"));
            post.setImageUrl(uploaded.get("secure_url").toString());
        }
        return postRepository.save(post);
    }

    @Override
    public Optional<Post> updatePost(Long id, Post post, MultipartFile imageFile) throws IOException {
        return postRepository.findById(id).map(existingPost -> {
            existingPost.setTitle(post.getTitle());
            existingPost.setContent(post.getContent());
            
            try {
                if (imageFile != null && !imageFile.isEmpty()) {
                    var uploaded = cloudinary.uploader().upload(imageFile.getBytes(), ObjectUtils.asMap("folder", "posts"));
                    existingPost.setImageUrl(uploaded.get("secure_url").toString());
                } else if (post.getImageUrl() != null) {
                    existingPost.setImageUrl(post.getImageUrl());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return postRepository.save(existingPost);
        });
    }

    @Override
    public boolean deletePost(Long id) {
        if (postRepository.existsById(id)) {
            postRepository.deleteById(id);
            return true;
        }
        return false;
    }
}