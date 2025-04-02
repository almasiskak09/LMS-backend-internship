package kz.bitlab.MainService.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LessonDto {

    private Long id;
    private String lessonName;
    private String lessonDescription;
    private String lessonContent;
    private int lessonOrder;
    private Long chapterId; //id chapter
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
}
