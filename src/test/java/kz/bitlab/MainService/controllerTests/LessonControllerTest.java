package kz.bitlab.MainService.controllerTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import kz.bitlab.MainService.dto.LessonDto;
import kz.bitlab.MainService.entity.Chapter;
import kz.bitlab.MainService.entity.Lesson;
import kz.bitlab.MainService.exceptions.NotFoundException;
import kz.bitlab.MainService.controller.LessonController;
import kz.bitlab.MainService.services.LessonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LessonController.class)
@TestPropertySource(properties = {
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration"
})
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
        lesson = new Lesson(1L,"Мапперы", "Descripton", "Very Long Text",1,chapter,date,date,null);
        lessonDto = new LessonDto(1L,"Мапперы", "Descripton", "Very Long Text",1,1L,date,date,null);
        SecurityContextHolder.getContext().setAuthentication(
                new TestingAuthenticationToken("test-user", "none", "ROLE_ADMIN")
        );
    }

    //Тест на Получение списка всех уроков
    @Test
    @WithMockUser(roles = "USER")
    void getAllLessons() throws Exception {
        List<LessonDto> lessonDtoList = List.of(
                new LessonDto(1L,"Lesson1", "Desc1","Very Long Text",1,1L,date,date,null),
                new LessonDto(2L,"Lesson2", "Desc2","Very Long Text",1,1L,date,date,null),
                new LessonDto(3L,"Lesson3", "Desc3","Very Long Text",1,1L,date,date,null)
        );

        when(lessonService.getAllLessons()).thenReturn(lessonDtoList);
        mockMvc.perform(get("/api/lesson"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].lessonName",is("Lesson1")))
                .andExpect(jsonPath("$[0].lessonDescription",is("Desc1")))
                .andExpect(jsonPath("$[2].lessonName",is("Lesson3")));
    }

    //Тест на Получение урока по ID
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

    //Тест на Получение урока по ID - не найден - НЕГАТИВНЫЙ СЦЕНАРИЙ
    @Test
    void getLessonById_NotFoundException() throws Exception {
        Long findId = 1L;
        when(lessonService.getLessonById(findId)).thenThrow(new NotFoundException("Урок по id: 999 - не существует"));

        mockMvc.perform(get("/api/lesson/{id}", findId))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is("Урок по id: 999 - не существует")))
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    //Тест на Получение списка уроков по ID главы
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

    //Тест на Получение списка уроков по ID главы - некорректный ID главы - НЕГАТИВНЫЙ СЦЕНАРИЙ
    @Test
    void getAllLessonsByChapterId_InvalidChapterId () throws Exception {
        Long chapterId = -1L;
        when(lessonService.getAllLessonsByChapterId(chapterId)).thenThrow(new IllegalArgumentException("Пожалуйста, укажите корректный ID главы."));
        mockMvc.perform(get("/api/lesson/chapter/{id}", chapterId))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is("Пожалуйста, укажите корректный ID главы.")))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    //Тест на Получение списка уроков по ID главы - пустой список уроков - НЕГАТИВНЫЙ СЦЕНАРИЙ
    @Test
    void getAllLessonsByChapterId_EmptyLessons() throws Exception {
        Long chapterId = 1L;
        when(lessonService.getAllLessonsByChapterId(chapterId)).thenThrow(new NotFoundException("Нет уроков по данному id главы: 1"));
        mockMvc.perform(get("/api/lesson/chapter/{id}", chapterId))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message",is("Нет уроков по данному id главы: 1")))
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    //Тест на Создание урока
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

    //Тест на создание урока, Указан неверный ID главы - НЕГАТИВНЫЙ СЦЕНАРИЙ
    @Test
    void createLesson_InvalidChapterId () throws Exception {
        when(lessonService.createLesson(ArgumentMatchers.any(LessonDto.class))).thenThrow(new IllegalArgumentException("Пожалуйста, укажите корректный ID главы."));
        mockMvc.perform(post("/api/lesson")
                .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(lessonDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is("Пожалуйста, укажите корректный ID главы.")))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    //Тест на создание урока, урок с таким названием уже существует - НЕГАТИВНЫЙ СЦЕНАРИЙ
    @Test
    void createExistsLessonName_DataIntegrityViolationException()throws Exception {
        when(lessonService.createLesson(ArgumentMatchers.any(LessonDto.class))).thenThrow(new DataIntegrityViolationException("Урок с таким названием уже существует"));
        mockMvc.perform(post("/api/lesson")
             .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(lessonDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is("Урок с таким названием уже существует")))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    //Тест на создание урока, отправлено пустое название урока - НЕГАТИВНЫЙ СЦЕНАРИЙ
    @Test
    void createLessonWithEmptyName_IllegalArgumentException() throws Exception {
        when(lessonService.createLesson(ArgumentMatchers.any(LessonDto.class))).thenThrow(new IllegalArgumentException("Название урока не может быть пустым"));
        mockMvc.perform(post("/api/lesson")
        .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(lessonDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message",is("Название урока не может быть пустым") ))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    //Тест на обновление урока
    @Test
    void updateLesson() throws Exception {
        LessonDto newLessonDto = new LessonDto(1L,"Exceptions", "Про исключения", "Very Long Text",5,1L,date,date,null);
        when(lessonService.updateLesson(ArgumentMatchers.any(LessonDto.class))).thenReturn(newLessonDto);

        mockMvc.perform(put("/api/lesson")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(newLessonDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.lessonName",is("Exceptions")))
                .andExpect(jsonPath("$.lessonOrder",is(5)));
    }

    //Тест на обновление урока - урок с таким Id не существует - НЕГАТИВНЫЙ СЦЕНАРИЙ
    @Test
    void updateLessonNotFoundById_NotFoundException() throws Exception {
        Long lessonId = 999L;
        when(lessonService.updateLesson(ArgumentMatchers.any(LessonDto.class))).thenThrow(new NotFoundException("Урок по id: 999 - не существует"));
        mockMvc.perform(put("/api/lesson")
        .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(lessonDto)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message",is("Урок по id: 999 - не существует")))
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    //Тест на обновление урока - отправлено пустое название урока - НЕГАТИВНЫЙ СЦЕНАРИЙ
    @Test
    void updateLessonWithEmptyName_IllegalArgumentException () throws Exception {
        when(lessonService.updateLesson(ArgumentMatchers.any(LessonDto.class))).thenThrow(new IllegalArgumentException("Название урока не может быть пустым"));
        mockMvc.perform(put("/api/lesson")
        .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(lessonDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is("Название урока не может быть пустым")))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    //Тест на обновление урока - урок с таким названием уже существует - НЕГАТИВНЫЙ СЦЕНАРИЙ
    @Test
    void updateLessonWithExistsName_DataIntegrityViolationException () throws Exception {
        when(lessonService.updateLesson(ArgumentMatchers.any(LessonDto.class))).thenThrow(new DataIntegrityViolationException("Урок с таким названием уже существует: Java"));
        mockMvc.perform(put("/api/lesson")
                .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(lessonDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message",is("Урок с таким названием уже существует: Java")))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    //Тест на удаление урока
    @Test
    void deleteLesson() throws Exception {
        Long findId = 1L;
        doNothing().when(lessonService).deleteLessonById(findId);
        mockMvc.perform(delete("/api/lesson/{id}", findId))
                .andExpect(status().isNoContent());
    }

    //Тест на удаление урока - урок не найден - НЕГАТИВНЫЙ СЦЕНАРИЙ
    @Test
    void deleteLessonNotFoundById_NotFoundException () throws Exception {
        Long findId = 999L;
        doThrow(new NotFoundException("Урок по id: 999 - не существует")).when(lessonService).deleteLessonById(findId);
        mockMvc.perform(delete("/api/lesson/{id}", findId))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

}
