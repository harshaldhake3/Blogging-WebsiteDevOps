package com.example.blog.web;

import com.example.blog.service.PostService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.example.blog.domain.Post;

@Controller
public class HomeController {

  private final PostService postService;

  public HomeController(PostService postService) { this.postService = postService; }

  @GetMapping({"/", "/posts"})
  public String home(Model model) {
    Page<Post> page = postService.listPublished(0, 20);
    model.addAttribute("posts", page.getContent());
    return "home";
  }

  @GetMapping("/login")
  public String login() {
    return "login";
  }
}
