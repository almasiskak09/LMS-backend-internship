package kz.bitlab.MainService.controllerTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import kz.bitlab.MainService.dto.ChapterDto;
import kz.bitlab.MainService.entity.Chapter;
import kz.bitlab.MainService.entity.Course;
import kz.bitlab.MainService.exceptions.NotFoundException;
import kz.bitlab.MainService.restApi.ChapterController;
import kz.bitlab.MainService.services.ChapterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(ChapterController.class)
public class ChapterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ChapterService chapterService;

    @Autowired
    private ObjectMapper objectMapper;

    private Chapter chapter;
    private ChapterDto chapterDto;
    private LocalDateTime date;
    private Course course;

    @BeforeEach
    void setUp() {
        date = LocalDateTime.of(2024,07,14,12,0);
        course = new Course(1L,"Java","1J",date,date,null);
        chapter = new Chapter(1L,"Основы,синтаксис языка","Long Text",1,course,date,date,null);
        chapterDto = new ChapterDto(1L,"Основы,синтаксис языка","Long Text",1,1L, date,date,null);
    }

    @Test
    void getAllChapters() throws Exception {
        List<ChapterDto> chapterDtoList = List.of(
                new ChapterDto(1L,"Основы,синтаксис языка","Small Long Text",1,1L,date,date,null),
                new ChapterDto(2L,"Переменные","Medium Long Text",1,1L,date,date,null),
                new ChapterDto(3L,"Try catch блоки","Very Long Text",1,1L,date,date,null)
        );

        when(chapterService.getAllChapters()).thenReturn(chapterDtoList);
        mockMvc.perform(get("/api/chapter"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$",hasSize(3)))
                .andExpect(jsonPath("$[1].chapterName", is("Переменные")))
                .andExpect(jsonPath("$[2].chapterDescription", is("Very Long Text") ));
    }

    @Test
    void getChapterById() throws Exception {
        Long findId = 1L;
        when(chapterService.getChapterById(findId)).thenReturn(chapterDto);
        mockMvc.perform(get("/api/chapter/{id}", findId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.chapterName", is("Основы,синтаксис языка")));
    }

    @Test
    void getChapterById_NotFoundException () throws Exception {
        Long findCourseId = 999L;
        when(chapterService.getChapterById(findCourseId)).thenThrow(new NotFoundException("Глава курса по id: 999 - не существует"));
        mockMvc.perform(get("/api/chapter/{id}", findCourseId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Глава курса по id: 999 - не существует")))
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.timestamp",is (notNullValue())));
    }

    @Test
    void getAllChaptersByCourseId() throws Exception {
        Long courseId = 1L;
        when(chapterService.getAllChaptersByCourseId(courseId)).thenReturn(List.of(chapterDto));
        mockMvc.perform(get("/api/chapter/course/{id}", courseId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$",hasSize(1)))
                .andExpect(jsonPath("$[0].chapterName", is("Основы,синтаксис языка")))
                .andExpect(jsonPath("$[0].chapterDescription", is("Long Text")));

    }

    @Test
    void getAllChaptersByCourseId_InvalidCourseId() throws Exception {
        Long courseId = 999L;
        when(chapterService.getAllChaptersByCourseId(courseId)).thenThrow(new IllegalArgumentException("Пожалуйста, укажите корректный ID курса."));
        mockMvc.perform(get("/api/chapter/course/{id}", courseId))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message",is("Пожалуйста, укажите корректный ID курса.")))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.timestamp",is (notNullValue())));
    }

    @Test
    void getAllChaptersByCourseId_EmptyChapter() throws Exception {
        Long courseId = 1L;
        when(chapterService.getAllChaptersByCourseId(courseId)).thenThrow(new NotFoundException("Нет глав по id курса: 1"));
        mockMvc.perform(get("/api/chapter/course/{id}", courseId))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is("Нет глав по id курса: 1")))
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.timestamp",is (notNullValue())));
    }

    @Test
    void createChapter() throws Exception {
        when(chapterService.createChapter(ArgumentMatchers.any(ChapterDto.class))).thenReturn(chapterDto);

        mockMvc.perform(post("/api/chapter")
                    .contentType(MediaType.APPLICATION_JSON)
                     .content(objectMapper.writeValueAsString(chapterDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.chapterName", is("Основы,синтаксис языка")));
    }

    @Test
    void createChapter_InvalidCourseId() throws Exception {
        when(chapterService.createChapter(ArgumentMatchers.any(ChapterDto.class))).thenThrow(new IllegalArgumentException("Пожалуйста, укажите корректный ID курса."));
        mockMvc.perform(post("/api/chapter")
        .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(chapterDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is("Пожалуйста, укажите корректный ID курса.")))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.timestamp",is (notNullValue())));
    }

    @Test
    void  createExistsChapterName_DataIntegrityViolationException() throws Exception {

        when(chapterService.createChapter(ArgumentMatchers.any(ChapterDto.class))).thenThrow(new DataIntegrityViolationException("Глава с таким названием уже существует"));
        mockMvc.perform(post("/api/chapter")
        .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(chapterDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is("Глава с таким названием уже существует")))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.timestamp",is (notNullValue())));
    }

    @Test
    void createChapterWithEmptyName_IllegalArgumentException() throws Exception {
        when(chapterService.createChapter(ArgumentMatchers.any(ChapterDto.class))).thenThrow(new IllegalArgumentException("Названия главы не может быть пустым"));
        mockMvc.perform(post("/api/chapter")
        .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(chapterDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is("Названия главы не может быть пустым")))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.timestamp",is (notNullValue())));
    }

    @Test
    void updateChapter() throws Exception {

        ChapterDto newChapterDto = new ChapterDto(1L,"Try Catch","Long Text",1,1L, date,date,null);

        when(chapterService.updateChapter(ArgumentMatchers.any(ChapterDto.class))).thenReturn(newChapterDto);
        mockMvc.perform(put("/api/chapter", newChapterDto.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newChapterDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.chapterName", is("Try Catch")));

    }

    @Test
    void updateChapterNotFoundById_NotFoundException() throws Exception {
        when(chapterService.updateChapter(ArgumentMatchers.any(ChapterDto.class))).thenThrow(new NotFoundException("Глава курса по id: 999 - не существует"));
        mockMvc.perform(put("/api/chapter")
                        .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(chapterDto)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Глава курса по id: 999 - не существует")))
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.timestamp",is (notNullValue())));


    }

    @Test
    void updateChapterWithEmptyName_IllegalArgumentException() throws Exception {
        when(chapterService.updateChapter(ArgumentMatchers.any(ChapterDto.class))).thenThrow(new IllegalArgumentException("Глава с таким названием уже существует"));

        mockMvc.perform(put("/api/chapter")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(chapterDto)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Глава с таким названием уже существует")))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.timestamp",is (notNullValue())));
    }

    @Test
    void updateChapterWithExistsName_DataIntegrityViolationException() throws Exception {
        when(chapterService.updateChapter(ArgumentMatchers.any(ChapterDto.class))).thenThrow(new DataIntegrityViolationException("Глава с таким названием уже существует"));
        mockMvc.perform(put("/api/chapter")
        .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(chapterDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is("Глава с таким названием уже существует")))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.timestamp",is (notNullValue())));
    }

    @Test
    void deleteChapter() throws Exception {
        Long findId = 1L;
        doNothing().when(chapterService).deleteChapterById(findId);
        mockMvc.perform(delete("/api/chapter/{id}", findId))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteChapterNotFoundById_NotFoundException() throws Exception {
        Long findId = 999L;
        doThrow(new NotFoundException("Глава курса по id: 999 - не существует")).when(chapterService).deleteChapterById(findId);
        mockMvc.perform(delete("/api/chapter/{id}", findId))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Глава курса по id: 999 - не существует")))
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.timestamp",is (notNullValue())));
    }
}
