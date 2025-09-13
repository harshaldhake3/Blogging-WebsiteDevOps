package com.example.blog.web;

import com.example.blog.domain.Post;
import com.example.blog.domain.User;
import com.example.blog.repository.UserRepository;
import com.example.blog.service.PostService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/posts")
@Validated
public class PostController {

  private final PostService posts;
  private final UserRepository users;

  public PostController(PostService posts, UserRepository users) {
    this.posts = posts; this.users = users;
  }

  @GetMapping("/{id}")
  public String view(@PathVariable Long id, Model model) {
    model.addAttribute("post", posts.get(id));
    return "post";
  }

  @GetMapping("/new")
  public String form(Model model) {
    model.addAttribute("post", new Post());
    return "post_form";
  }

  @PostMapping
  public String create(@ModelAttribute Post post,
                       @RequestParam(value = "files", required = false) List<MultipartFile> files,
                       @AuthenticationPrincipal UserDetails userDetails) throws IOException {
    User author = users.findByUsername(userDetails.getUsername()).orElseThrow();
    posts.create(post, author, files);
    return "redirect:/";
  }
}
