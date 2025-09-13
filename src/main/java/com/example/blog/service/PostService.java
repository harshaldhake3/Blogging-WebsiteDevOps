package com.example.blog.service;

import com.example.blog.domain.Attachment;
import com.example.blog.domain.Post;
import com.example.blog.domain.User;
import com.example.blog.repository.AttachmentRepository;
import com.example.blog.repository.PostRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;

@Service
public class PostService {

  private final PostRepository posts;
  private final AttachmentRepository attachments;

  @Value("${app.upload.dir:/data/uploads}")
  private String uploadDir;

  public PostService(PostRepository posts, AttachmentRepository attachments) {
    this.posts = posts;
    this.attachments = attachments;
  }

  public Page<Post> listPublished(int page, int size) {
    Pageable pageable = PageRequest.of(page, size);
    return posts.findByPublishedTrue(pageable);
  }

  public Post get(Long id) {
    return posts.findById(id).orElseThrow();
  }

  @Transactional
  public Post create(Post post, User author, List<MultipartFile> files) throws IOException {
    post.setAuthor(author);
    post.setCreatedAt(Instant.now());
    post.setUpdatedAt(Instant.now());
    post.setPublished(true);
    Post saved = posts.save(post);

    Path base = Path.of(uploadDir, String.valueOf(saved.getId()));
    Files.createDirectories(base);
    if (files != null) {
      for (MultipartFile f : files) {
        if (f.isEmpty()) continue;
        Path dest = base.resolve(f.getOriginalFilename());
        Files.copy(f.getInputStream(), dest);
        Attachment a = Attachment.builder()
          .filename(f.getOriginalFilename())
          .contentType(f.getContentType())
          .size(f.getSize())
          .storagePath(dest.toString())
          .post(saved)
          .build();
        attachments.save(a);
        saved.getAttachments().add(a);
      }
    }
    return saved;
  }
}
