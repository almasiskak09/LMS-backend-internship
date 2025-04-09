package kz.bitlab.MainService.controllerTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import kz.bitlab.MainService.dto.LessonDto;
import kz.bitlab.MainService.entity.Chapter;
import kz.bitlab.MainService.entity.Lesson;
import kz.bitlab.MainService.restApi.LessonController;
import kz.bitlab.MainService.services.LessonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LessonController.class)
public class LessonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LessonService lessonService;

    @Autowired
    private ObjectMapper objectMapper;

    private Lesson lesson;
    private LessonDto lessonDto;
    private LocalDateTime date;
    private Chapter chapter;

    @BeforeEach
    void setUp() {
        date = LocalDateTime.of(2024,07,14,12,0);
        chapter = new Chapter(1L,"Основы,синтаксис языка", "Long Text",1,null,date,date,null);
        lesson = new Lesson(1L,"Мапперы", "Descripton", "Very Long Text",1,chapter,date,date);
        lessonDto = new LessonDto(1L,"Мапперы", "Descripton", "Very Long Text",1,1L,date,date);
    }

    @Test
    void getAllLessons() throws Exception {
        List<LessonDto> lessonDtoList = List.of(
                new LessonDto(1L,"Lesson1", "Desc1","Very Long Text",1,1L,date,date),
                new LessonDto(2L,"Lesson2", "Desc2","Very Long Text",1,1L,date,date),
                new LessonDto(3L,"Lesson3", "Desc3","Very Long Text",1,1L,date,date)
        );

        when(lessonService.getAllLessons()).thenReturn(lessonDtoList);
        mockMvc.perform(get("/api/lesson"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].lessonName",is("Lesson1")))
                .andExpect(jsonPath("$[0].lessonDescription",is("Desc1")))
                .andExpect(jsonPath("$[2].lessonName",is("Lesson3")));
    }

    @Test
    void getLessonById() throws Exception {
        Long findId = 1L;
        when(lessonService.getLessonById(findId)).thenReturn(lessonDto);
        mockMvc.perform(get("/api/lesson/{id}", findId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.lessonName",is("Мапперы")))
                .andExpect(jsonPath("$.lessonDescription",is("Descripton")));
    }

    @Test
    void getLessonsByChapterId() throws Exception {
        Long chapterId = 1L;
        List<LessonDto> lessonDtoList = List.of(lessonDto);

        when(lessonService.getAllLessonsByChapterId(chapterId)).thenReturn(lessonDtoList);
        mockMvc.perform(get("/api/lesson/chapter/{id}", chapterId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].lessonName",is("Мапперы")))
                .andExpect(jsonPath("$[0].lessonDescription",is("Descripton")));
    }
    @Test
    void createLesson() throws Exception {

        when(lessonService.createLesson(ArgumentMatchers.any(LessonDto.class))).thenReturn(lessonDto);
        mockMvc.perform(post("/api/lesson")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(lessonDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.lessonName",is("Мапперы")))
                .andExpect(jsonPath("$.lessonOrder",is(1)));
    }

    @Test
    void updateLesson() throws Exception {
        LessonDto newLessonDto = new LessonDto(1L,"Exceptions", "Про исключения", "Very Long Text",5,1L,date,date);
        when(lessonService.updateLesson(ArgumentMatchers.any(LessonDto.class))).thenReturn(newLessonDto);

        mockMvc.perform(put("/api/lesson")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(newLessonDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.lessonName",is("Exceptions")))
                .andExpect(jsonPath("$.lessonOrder",is(5)));
    }

    @Test
    void deleteLesson() throws Exception {
        Long findId = 1L;
        doNothing().when(lessonService).deleteLessonById(findId);
        mockMvc.perform(delete("/api/lesson/{id}", findId))
                .andExpect(status().isNoContent());
    }

}
