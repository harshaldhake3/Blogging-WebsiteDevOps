package com.example.blog.web;

import com.example.blog.domain.User;
import com.example.blog.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@Controller
@RequestMapping("/admin")
public class AdminController {

  private final UserRepository users;
  private final PasswordEncoder encoder;

  public AdminController(UserRepository users, PasswordEncoder encoder) {
    this.users = users; this.encoder = encoder;
  }

  @GetMapping
  public String dashboard(Model model) {
    model.addAttribute("users", users.findAll());
    return "admin";
  }

  @PostMapping("/seed")
  @ResponseBody
  public String seedAdmin() {
    if (!users.existsByUsername("admin")) {
      User u = User.builder()
        .username("admin").email("admin@example.com")
        .password(encoder.encode("admin123"))
        .roles(Set.of("ADMIN","USER")).enabled(true).build();
      users.save(u);
      return "Admin created: admin / admin123";
    }
    return "Admin already exists";
  }
}
