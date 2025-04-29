package kz.bitlab.MainService.dto;

import kz.bitlab.MainService.entity.Lesson;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AttachmentDto {

    private Long id;
    private String name;
    private String url;
    private Long lessonId;
    private LocalDateTime createdTime;
}
