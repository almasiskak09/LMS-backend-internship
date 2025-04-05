package kz.bitlab.MainService.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CourseDto {

    private Long id;

    private String courseName;
    private String courseDescription;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
    private List<ChapterDto> chapterDtoList;
}
