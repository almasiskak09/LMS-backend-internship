package kz.bitlab.MainService.controllerTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import kz.bitlab.MainService.dto.CourseDto;
import kz.bitlab.MainService.entity.Course;
import kz.bitlab.MainService.exceptions.NotFoundException;
import kz.bitlab.MainService.restApi.CourseController;
import kz.bitlab.MainService.services.CourseService;
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

@WebMvcTest(CourseController.class)
public class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CourseService courseService;

    @Autowired
    private ObjectMapper objectMapper;

    private Course course;
    private CourseDto courseDto;
    private LocalDateTime date;



    @BeforeEach
    void setUp() {

        date = LocalDateTime.of(2024, 4, 7, 12, 0);
        course = new Course(1L, "Java", "1J", date, date, null);
        courseDto = new CourseDto(1L, "Java", "1J", date, date, null);
    }

    @Test
    void getAllCourses() throws Exception {
        List<CourseDto> courseDtoList = List.of(
                new CourseDto(1L, "Java", "1J", date, date, null),
                new CourseDto(2L, "Python", "2P", date, date, null),
                new CourseDto(3L, "C#", "3C", date, date, null)
        );

        when(courseService.getAllCourses()).thenReturn(courseDtoList);
        mockMvc.perform(get("/api/course"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].courseName", is("Java")))
                .andExpect(jsonPath("$[1].courseDescription", is("2P")));
    }

    @Test
    void getCourseById() throws Exception {
        Long findCourseId = 1L;
        when(courseService.getCourseById(findCourseId)).thenReturn(courseDto);

        mockMvc.perform(get("/api/course/{id}", findCourseId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.courseName", is("Java")))
                .andExpect(jsonPath("$.courseDescription", is("1J")));

    }

    @Test
    void getCourseById_NotFoundException() throws Exception {
        Long findCourseId = 999L;
        when(courseService.getCourseById(findCourseId)).thenThrow(new NotFoundException("Курс с id: 999 - не существует"));
        mockMvc.perform(get("/api/course/{id}", findCourseId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Курс с id: 999 - не существует")))
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.timestamp",notNullValue()));
    }

    @Test
    void createCourse() throws Exception {
        when(courseService.createCourse(ArgumentMatchers.any(CourseDto.class))).thenReturn(courseDto);

        mockMvc.perform(post("/api/course")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(courseDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.courseName", is("Java")));
    }

    @Test
    void createExistsCourseName_handleDataIntegrityViolationException() throws Exception {
        when(courseService.createCourse(ArgumentMatchers.any(CourseDto.class))).thenThrow(new DataIntegrityViolationException("Курс с таким названием уже существует: Java"));

        mockMvc.perform(post("/api/course")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Курс с таким названием уже существует: Java")))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.timestamp",notNullValue()));
    }

    @Test
    void createCourseWithEmptyName_IllegalArgumentException() throws Exception {
        when(courseService.createCourse(ArgumentMatchers.any(CourseDto.class))).thenThrow(new IllegalArgumentException("Названия курса не может быть пустым"));

        mockMvc.perform(post("/api/course")
                .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(courseDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message",is("Названия курса не может быть пустым")))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.timestamp",notNullValue()));

    }

    @Test
    void updateCourse() throws Exception {
        CourseDto updateCourseDto = new CourseDto(1L, "Go", "1G", date, date, null);

        when(courseService.updateCourse(ArgumentMatchers.any(CourseDto.class))).thenReturn(updateCourseDto);

        mockMvc.perform(put("/api/course",updateCourseDto.getId() )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateCourseDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.courseName", is("Go")))
                .andExpect(jsonPath("$.courseDescription",is("1G")));
    }

    @Test
    void updateCourseNotFoundById_NotFoundException() throws Exception {
        Long findCourseId = 999L;
        when(courseService.updateCourse(ArgumentMatchers.any(CourseDto.class))).thenThrow(new NotFoundException("Курс с id: 999 - не существует"));

        mockMvc.perform(put("/api/course")
                .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(courseDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Курс с id: 999 - не существует")))
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.timestamp",notNullValue()));
    }

    @Test
    void updateCourseWithEmptyName_IllegalArgumentException() throws Exception {
        when(courseService.updateCourse(ArgumentMatchers.any(CourseDto.class))).thenThrow(new IllegalArgumentException("Названия курса не может быть пустым"));

        mockMvc.perform(put("/api/course")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message",is("Названия курса не может быть пустым")))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.timestamp",notNullValue()));

    }

    @Test
    void updateCourseWithExistsName_handleDataIntegrityViolationException()throws Exception {
        when(courseService.updateCourse(ArgumentMatchers.any(CourseDto.class))).thenThrow(new DataIntegrityViolationException("Курс с таким названием уже существует: Java"));

        mockMvc.perform(put("/api/course")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Курс с таким названием уже существует: Java")))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.timestamp",notNullValue()));
    }

    @Test
    void deleteCourse() throws Exception {
        Long findCourseId = 1L;
        doNothing().when(courseService).deleteCourseById(findCourseId);
        mockMvc.perform(delete("/api/course/{id}", findCourseId))
                .andExpect(status().isNoContent());
    } //делать через do.Comand

    @Test
    void deleteCourseNotFoundById_NotFoundException() throws Exception {
        Long findCourseId = 999L;

        doThrow(new NotFoundException("Курс с id: 999 - не существует")).when(courseService).deleteCourseById(findCourseId);

        mockMvc.perform(delete("/api/course/{id}", findCourseId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Курс с id: 999 - не существует")))
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.timestamp",notNullValue()));
    }
}
