package com.example.blog.service;

import com.example.blog.domain.User;
import com.example.blog.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class DbUserDetailsService implements UserDetailsService {

  private final UserRepository users;

  public DbUserDetailsService(UserRepository users) {
    this.users = users;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    com.example.blog.domain.User u = users.findByUsername(username)
      .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    var builder = User.withUsername(u.getUsername()).password(u.getPassword()).disabled(!u.isEnabled());
    if (u.getRoles() != null) {
      u.getRoles().forEach(r -> builder.roles(r.replace("ROLE_", "")));
    } else {
      builder.roles("USER");
    }
    return builder.build();
  }
}
