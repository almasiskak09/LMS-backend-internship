package kz.bitlab.MainService.controllerTestIT;

import jakarta.transaction.Transactional;
import kz.bitlab.MainService.AbstractTestIntegrationIT;
import kz.bitlab.MainService.dto.LessonDto;
import kz.bitlab.MainService.entity.Chapter;
import kz.bitlab.MainService.entity.Lesson;
import kz.bitlab.MainService.exceptions.NotFoundException;
import kz.bitlab.MainService.repository.LessonRepository;
import kz.bitlab.MainService.restApi.LessonController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Sql(scripts = {"classpath:/db/test/V1__insert.sql"},executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {"classpath:/db/test/clean.sql"},executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@Transactional
public class LessonControllerTestIT extends AbstractTestIntegrationIT {

    @Autowired
    private LessonController lessonController;

    @Autowired
    private LessonRepository lessonRepository;

    private LocalDateTime date;

    @BeforeEach
    void setUp() {
        date = LocalDateTime.of(2024,07,14,12,0);
    }

    @Test
    void getAllLessons() {
        ResponseEntity<List<LessonDto>> response = lessonController.getAllLessons();
        List<LessonDto> lessonDtoList = response.getBody();

        assertNotNull(lessonDtoList);
        assertEquals(2, lessonDtoList.size());
        assertEquals("Try-catch", lessonDtoList.get(1).getLessonName());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getLessonById() {
        Long lessonId = 2L;
        ResponseEntity<LessonDto> response = lessonController.getLessonById(lessonId);
        LessonDto lessonDto = response.getBody();

        assertNotNull(lessonDto);
        assertEquals("Try-catch", lessonDto.getLessonName());
        assertEquals(lessonId, lessonDto.getId());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getLessonByChapterId(){
        Long chapterId = 2L;
        ResponseEntity<LessonDto> lessonDto = lessonController.getLessonById(chapterId);
        LessonDto lessonDto1 = lessonDto.getBody();

        assertNotNull(lessonDto1);
        assertEquals("Try-catch", lessonDto1.getLessonName());
        assertEquals(chapterId, lessonDto1.getChapterId());
        assertEquals(HttpStatus.OK, lessonDto.getStatusCode());
    }

    @Test
    void createLesson() {
        LessonDto lesson = new LessonDto(null,"Spring Boot","Spring Security","All Security Text",1,1L,date,date);
        ResponseEntity<LessonDto> response = lessonController.createLesson(lesson);
        LessonDto lessonDto = response.getBody();

        Lesson foundLesson = lessonRepository.findById(lessonDto.getId()).orElseThrow(()-> new NotFoundException("Not Found"));
        assertNotNull(foundLesson);
        assertEquals("Spring Boot", foundLesson.getLessonName());
        assertEquals(3, foundLesson.getId());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

    }

    @Test
    void updateLesson() {
        LessonDto lesson = new LessonDto(1L,"Update Java","Update Spring ","All Update Text",1,1L,date,date);
        ResponseEntity<LessonDto> updateResponse = lessonController.updateLesson(lesson);
        LessonDto lessonDto = updateResponse.getBody();

        Lesson lessonFound = lessonRepository.findById(lessonDto.getId()).orElseThrow(null);
        assertNotNull(lessonFound);
        assertEquals("Update Java", lessonFound.getLessonName());
        assertEquals(1, lessonFound.getId());
        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());

    }

    @Test
    void deleteLesson() {
        Long lessonId = 2L;
        ResponseEntity<Void> deleteResponse = lessonController.deleteLessonById(lessonId);

        assertFalse(lessonRepository.existsById(lessonId));
        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());
    }
}
