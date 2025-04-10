package kz.bitlab.MainService.controllerTestIT;

import jakarta.transaction.Transactional;
import kz.bitlab.MainService.AbstractTestIntegrationIT;
import kz.bitlab.MainService.dto.ChapterDto;
import kz.bitlab.MainService.entity.Chapter;
import kz.bitlab.MainService.repository.ChapterRepository;
import kz.bitlab.MainService.restApi.ChapterController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Sql(scripts = {"classpath:/db/test/V1__insert.sql"},executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {"classpath:/db/test/clean.sql"},executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@Transactional
public class ChapterControllerTestIT extends AbstractTestIntegrationIT {

    @Autowired
    private ChapterController chapterController;

    @Autowired
    private ChapterRepository chapterRepository;
    
    private LocalDateTime date;
    @BeforeEach
    void setUp() {
        date = LocalDateTime.of(2024, 4, 7, 12, 0);
    }

    @Test
    void getAllChapters() {
        ResponseEntity<List<ChapterDto>> chapters= chapterController.getAllChapters();
        List<ChapterDto> response = chapters.getBody();

        assertNotNull(response);
        assertTrue(response.size() >= 2);
        assertEquals("Условия, условные операторы",response.get(1).getChapterName());
        assertEquals(1, response.get(0).getId());
        assertEquals(HttpStatus.OK, chapters.getStatusCode());

    }

    @Test
    void getChapterById() {
        Long findId = 2L;
        ResponseEntity<ChapterDto> chapterDto = chapterController.getChapterById(findId);
        ChapterDto response = chapterDto.getBody();

        assertNotNull(response);
        assertEquals(findId, response.getId());
        assertEquals("Условия, условные операторы", response.getChapterName());
        assertEquals(1,response.getCourseId());
        assertEquals(HttpStatus.OK, chapterDto.getStatusCode());

    }

    @Test
    void createChapter() {
        Long findId = 3L;
        ChapterDto chapterDto = new ChapterDto(null,"New Chapter Name", "New Chapter Description",1,1L,date,date,null);
        ResponseEntity<ChapterDto> response = chapterController.createChapter(chapterDto);

        Chapter createdDto = chapterRepository.findById(findId).orElseThrow(null);
        assertNotNull(createdDto);
        assertEquals("New Chapter Name", createdDto.getChapterName());
        assertEquals("New Chapter Description", createdDto.getChapterDescription());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void updateChapter() {
        Long findId = 1L;
        ChapterDto chapterDto = new ChapterDto(1L,"New Chapter Update Name", "New Update Chapter Description",1,1L,date,date,null);
        ResponseEntity<ChapterDto> response = chapterController.updateChapter(chapterDto);

        Chapter updatedDto = chapterRepository.findById(findId).orElseThrow(null);
        assertNotNull(updatedDto);
        assertEquals("New Chapter Update Name", updatedDto.getChapterName());
        assertEquals("New Update Chapter Description", updatedDto.getChapterDescription());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        }

    @Test
    void deleteChapter() {
        Long findId = 1L;
        ResponseEntity<Void> delete = chapterController.deleteChapterById(findId);
        assertEquals(HttpStatus.NO_CONTENT, delete.getStatusCode());
    }

}

