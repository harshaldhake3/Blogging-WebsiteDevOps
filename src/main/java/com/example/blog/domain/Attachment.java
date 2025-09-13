package com.example.blog.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "attachments")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Attachment {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String filename;
  private String contentType;
  private long size;
  private String storagePath;

  @ManyToOne(fetch = FetchType.LAZY)
  private Post post;
}
