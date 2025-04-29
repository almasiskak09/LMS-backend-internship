package kz.bitlab.MainService.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "attachments")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Attachment {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @Column(name = "attachment_name")
   private String name;

   @Column(name = "url")
   private String url;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "lesson_id",nullable = false)
   private Lesson lesson;

   @Column(name = "created_time")
   private LocalDateTime createdTime;
}
