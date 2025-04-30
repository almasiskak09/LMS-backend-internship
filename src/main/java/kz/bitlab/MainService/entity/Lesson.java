package kz.bitlab.MainService.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "lesson")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "lesson_name")
    private String lessonName;

    @Column(name = "lesson_description")
    private String lessonDescription;

    @Column(name = "lesson_content")
    private String lessonContent;

    @Column(name = "lesson_order")
    private int lessonOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id", nullable = false)
    private Chapter chapter;

    @CreationTimestamp
    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @UpdateTimestamp
    @Column(name = "updated_time", updatable = false)
    private LocalDateTime updatedTime;

    @OneToMany(mappedBy = "lesson",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<Attachment> attachments;
}
