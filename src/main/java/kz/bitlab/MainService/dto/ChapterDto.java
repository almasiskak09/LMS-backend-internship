package kz.bitlab.MainService.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChapterDto {

    private Long id;
    private String chapterName;
    private String chapterDescription;
    private int chapterOrder;
    private Long courseId;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
    private List<LessonDto> lessonDtoList; //список dtoLesson

}
