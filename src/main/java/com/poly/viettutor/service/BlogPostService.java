package com.poly.viettutor.service;

import com.poly.viettutor.model.BlogPost;
import com.poly.viettutor.repository.BlogPostRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BlogPostService {

    private final BlogPostRepository blogPostRepository;

    public BlogPostService(BlogPostRepository blogPostRepository) {
        this.blogPostRepository = blogPostRepository;
    }

    public List<BlogPost> findAll() {
        return blogPostRepository.findAll();
    }

    public Optional<BlogPost> findById(Integer id) {
        return blogPostRepository.findById(id);
    }

    public BlogPost save(BlogPost blogPost) {
        return blogPostRepository.save(blogPost);
    }

    public void deleteById(Integer id) {
        blogPostRepository.deleteById(id);
    }

    public Page<BlogPost> findAll(Pageable pageable) {
        return blogPostRepository.findAll(pageable);
    }

    public List<BlogPost> getAny3Posts() {
        return blogPostRepository.findTop3ByOrderByPostIdDesc();
    }

    public long countAllPosts() {
        return blogPostRepository.count();
    }
}
