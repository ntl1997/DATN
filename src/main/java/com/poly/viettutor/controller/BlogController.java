package com.poly.viettutor.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.poly.viettutor.model.BlogPost;
import com.poly.viettutor.service.BlogPostService;

@Controller
public class BlogController {

    @Autowired
    private BlogPostService bService;

    @GetMapping("/bai-viet")
    public String showBlog(Model model, @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "3") int size) {
        long totalPosts = bService.countAllPosts();
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<BlogPost> blogPage = bService.findAll(pageable);
        // List<BlogPost> bPosts = bService.findAll();
        // model.addAttribute("blogPosts", bPosts);
        // model.addAttribute("content", "client/blog/blog-list");
        // model.addAttribute("title", "Bài viết");
        model.addAttribute("blogPage", blogPage);
        model.addAttribute("totalPosts", totalPosts);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", blogPage.getTotalPages());
        model.addAttribute("content", "client/blog/blog-list");
        model.addAttribute("title", "Bài viết");
        return "client/layout/index";
    }

    @GetMapping("/bai-viet/{id}")
    public String showBlogDetails(Model model, @RequestParam("title") String title, @PathVariable("id") Integer id) {
        Optional<BlogPost> blogOptional = bService.findById(id);
        List<BlogPost> any3Posts = bService.getAny3Posts();
        if (blogOptional.isPresent()) {
            BlogPost post = blogOptional.get();
            String creatorName = post.getCreatedBy().getFullname();
            String creatorImage = post.getCreatedBy().getImage();
            model.addAttribute("any3Posts", any3Posts);
            model.addAttribute("blogPost", post);
            model.addAttribute("creatorImage", creatorImage);
            model.addAttribute("creatorName", creatorName);
            model.addAttribute("title", title);
            model.addAttribute("content", "client/blog/blog-details");
            return "client/layout/index";
        } else {
            return null;
        }
    }

}
