package com.poly.viettutor.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.poly.viettutor.model.BlogPost;
import com.poly.viettutor.service.BlogPostService;

@Controller
public class BlogController {

    @Autowired
    private BlogPostService bService;

    @GetMapping("/bai-viet")
    public String showBlog(Model model) {
        List<BlogPost> bPosts = bService.findAll();
        model.addAttribute("blogPosts", bPosts);
        model.addAttribute("content", "client/blog/blog-list");
        model.addAttribute("title", "Bài viết");
        return "client/layout/index";
    }

    // @GetMapping("/blog-details")
    // public String showBlogDetails(Model model, @RequestParam("title") String
    // title) {
    // model.addAttribute("content", "client/blog/blog-details");
    // model.addAttribute("title", title);
    // return "client/layout/index";
    // }

}
